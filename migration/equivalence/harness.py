#!/usr/bin/env python3
"""Run the same ordered service inputs through committed original and REST boundaries."""
import argparse
import base64
from datetime import datetime, timezone
from decimal import Decimal
import hashlib
import json
import os
from pathlib import Path
import re
import secrets
import signal
import ssl
import subprocess
import sys
import time
import urllib.error
import urllib.request

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'tools'))
from eca_catalog import generate
from compare import Normalizer, bind_created_keys, delta, encoded, mismatches, response_contract
from setup import CONTAINER, ROOT, STATE, command, setup, sql

HERE = Path(__file__).resolve().parent
OUTPUT = ROOT / 'docs/migration/equivalence'
SSL = ssl._create_unverified_context()


def request(url, body=None, user='admin'):
    headers = {'Content-Type': 'application/json'}
    token = os.environ['EQUIV_PASSWORD']
    if url.startswith('https://127.0.0.1:18443/'):
        headers['X-Equivalence-Token'] = token
    else:
        headers['Authorization'] = 'Basic ' + base64.b64encode(f'{user}:{token}'.encode()).decode()
    payload = None if body is None else json.dumps(body, default=str).encode()
    req = urllib.request.Request(url, payload, headers)
    try:
        response = urllib.request.urlopen(req, timeout=90, context=SSL)
    except urllib.error.HTTPError as error:
        response = error
    text = response.read().decode()
    try:
        data = json.loads(text, parse_float=Decimal)
    except json.JSONDecodeError:
        raise RuntimeError(f'Non-JSON response from {url}: HTTP {response.code}; see server log') from None
    return {'httpStatus': response.code, 'response': data, 'wireBody': text}


def snapshot(database, tables):
    if any(not re.fullmatch('[a-z][a-z0-9_]*', table) for table in tables):
        raise ValueError('Snapshot names must be PostgreSQL identifiers')
    args = ','.join(f"'{table}',(select coalesce(jsonb_agg(to_jsonb(t)), '[]'::jsonb) from {table} t)"
                    for table in tables)
    return json.loads(sql(database, f'select jsonb_build_object({args})'), parse_float=Decimal)


def resolve(value, bindings):
    if isinstance(value, str) and value.startswith('$'):
        return bindings[value[1:]]
    if isinstance(value, list):
        return [resolve(item, bindings) for item in value]
    if isinstance(value, dict):
        return {key: resolve(item, bindings) for key, item in value.items()}
    return value


def successful(result):
    response = result['response']
    return result['httpStatus'] < 400 and response.get('responseMessage', 'success') not in ('error', 'fail')


def assertions(step, rows, bindings, result):
    failures = []
    expected_error = step.get('expect', 'success') == 'error'
    if successful(result) == expected_error:
        failures.append('Expected ' + ('error' if expected_error else 'success'))
    for assertion in step.get('assertRows', []):
        try:
            where = resolve(assertion['where'], bindings)
        except KeyError as error:
            failures.append(f'Cannot assert row: missing captured reference {error}')
            continue
        matches = [row for row in rows[assertion['table']]
                   if all(str(row.get(key)) == str(value) for key, value in where.items())]
        if len(matches) != assertion.get('count', 1):
            failures.append(f"{assertion['table']} expected {assertion.get('count', 1)} matching rows; got {len(matches)}")
    return failures


def capture_created_rows(before, after, specification):
    captured = {}
    for table, fields in specification.items():
        previous = {tuple(row.get(field) for field in fields) for row in before[table]}
        created = [row for row in after[table] if tuple(row.get(field) for field in fields) not in previous]
        if len(created) == 1:
            captured.update({label: created[0][field] for field, label in fields.items()})
    return captured


def run_cases(manifest, until=None):
    contracts = generate({})['parameters']
    bindings = {'old': {}, 'new': {}}
    normalizer = Normalizer()
    results = []
    accepted_path = HERE / 'accepted-differences.json'
    accepted = json.loads(accepted_path.read_text()) if accepted_path.exists() else {}
    for index, step in enumerate(manifest['steps'][:until]):
        row = {'id': step['id'], 'flow': step['flow'], 'service': step['service'], 'runAs': step['runAs']}
        before = {side: snapshot('equiv_' + side, step['tables']) for side in ['old', 'new']}
        after = {}
        for side in ['old', 'new']:
            values = resolve(step['input'], bindings[side])
            normalizer.preserve_input_times(values)
            if side == 'old':
                result = request('https://127.0.0.1:18443/equivalence/control/dispatch',
                                 {'service': step['service'], 'input': values, 'runAs': step['runAs'],
                                  'entityLists': step.get('entityLists', {})})
            else:
                if step['component'] == 'humanres':
                    url = 'http://127.0.0.1:18091/api/humanres/services/' + step['service']
                else:
                    url = f"http://127.0.0.1:18090/api/equivalence/{step['component']}/services/{step['service']}"
                result = request(url, values, step['runAs'])
            row[side] = result
            after[side] = snapshot('equiv_' + side, step['tables'])
            bindings[side].update(capture_created_rows(before[side], after[side], step.get('captureCreatedRows', {})))
            for field, label in step.get('capture', {}).items():
                if field in result['response']:
                    bindings[side][label] = result['response'][field]
        for field, label in step.get('capture', {}).items():
            normalizer.bind(field, bindings['old'].get(label), bindings['new'].get(label), label)
        for table, fields in manifest.get('generatedKeys', {}).items():
            if table not in step['tables']:
                continue
            bind_created_keys(normalizer, step['id'], table, fields, before, after)
        comparisons = {}
        responses, info = response_contract({side: row[side]['response'] for side in ['old', 'new']},
                                            contracts[step['service']],
                                            {side: successful(row[side]) for side in ['old', 'new']})
        row['responseInfo'] = info
        for side in ['old', 'new']:
            changes = delta(before[side], after[side], normalizer, side)
            failures = assertions(step, after[side], bindings[side], row[side])
            if step.get('expect') == 'error' and any(change['added'] or change['removed'] for change in changes.values()):
                failures.append('Error changed persisted business rows')
            row[side]['assertionFailures'] = failures
            row[side]['changes'] = changes
            comparisons[side] = {'response': normalizer.normalize(responses[side], side), 'changes': changes}
        row['differences'] = mismatches(comparisons['old'], comparisons['new'])
        row['fingerprint'] = hashlib.sha256(encoded(row['differences']).encode()).hexdigest()
        failed = bool(row['old']['assertionFailures'] or row['new']['assertionFailures'])
        row['status'] = 'MISMATCH' if row['differences'] or failed else 'MATCH'
        acceptance = accepted.get(step['id'], {})
        if row['status'] == 'MISMATCH' and not failed and acceptance.get('fingerprint') == row['fingerprint']:
            row['status'] = 'ACCEPTED_DIFFERENCE'
            row['acceptedReason'] = acceptance['reason']
        error_paths = acceptance.get('errorEnvelopePaths', [])
        if (row['status'] == 'MISMATCH' and not failed and step.get('expect') == 'error'
                and error_paths and set(error_paths) == {item['path'] for item in row['differences']}
                and all(path in ('/response/errorMessage', '/response/errorMessageList', '/response/errorMessageMap')
                        for path in error_paths)):
            row['status'] = 'ACCEPTED_DIFFERENCE'
            row['acceptedReason'] = acceptance['reason']
        results.append(row)
        print(f"{index + 1:02d} {row['id']}: {row['status']} ({len(row['differences'])} differences)", flush=True)
        (STATE / 'results-in-progress.json').write_text(json.dumps(results, indent=2, default=str) + '\n')
        if failed:
            print(json.dumps({side: row[side]['assertionFailures'] for side in ['old', 'new']}, default=str), flush=True)
        expected_captures = list(step.get('capture', {}).values()) + [label
                for fields in step.get('captureCreatedRows', {}).values() for label in fields.values()]
        missing = [label for label in expected_captures
                   if label not in bindings['old'] or label not in bindings['new']]
        if missing:
            raise RuntimeError(f"{step['id']} cannot bind {missing}; inspect results-in-progress.json")
    return results


def start_servers():
    processes = []
    classpath = (STATE / 'old-classpath.txt').read_text()
    args = json.loads((STATE / 'old-jvm-args.json').read_text())
    launches = [
        ('old', ['java', *args, '-Duser.timezone=UTC', '-XX:ActiveProcessorCount=4', '-cp', classpath,
                 'org.apache.ofbiz.base.start.Start']),
        ('new', ['java', '-Xmx1536m', '-XX:ActiveProcessorCount=4', '-Duser.timezone=UTC',
                 f'-Dofbiz.root={ROOT}', '-jar', str(HERE / 'build/libs/equivalence.jar')]),
        ('humanres', ['java', '-Xmx768m', '-XX:ActiveProcessorCount=4', '-Duser.timezone=UTC', '-jar',
                      str(next((ROOT / 'migration/humanres-springboot/build/libs').glob('*SNAPSHOT.jar'))),
                      '--spring.profiles.active=equivalence',
                      f'--spring.config.additional-location=file:{HERE}/humanres-equivalence.yml']),
    ]
    try:
        for name, args in launches:
            log = (STATE / f'{name}.log').open('w')
            process = subprocess.Popen(args, cwd=ROOT, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
            log.close()
            processes.append(process)
        for process, (name, _) in zip(processes, launches):
            port = {'old': 18443, 'new': 18090, 'humanres': 18091}[name]
            url = f"{'https' if name == 'old' else 'http'}://127.0.0.1:{port}/"
            for _ in range(180):
                if process.poll() is not None:
                    raise RuntimeError(f'{name} exited; inspect {STATE}/{name}.log')
                try:
                    urllib.request.urlopen(url, context=SSL, timeout=1)
                    break
                except urllib.error.HTTPError:
                    break
                except (urllib.error.URLError, TimeoutError):
                    time.sleep(1)
            else:
                raise RuntimeError(f'{name} startup timed out; inspect its log')
        return processes
    except BaseException:
        stop_servers(processes)
        raise


def stop_servers(processes):
    for process in processes:
        if process.poll() is None:
            os.killpg(process.pid, signal.SIGTERM)
    for process in processes:
        try:
            process.wait(timeout=30)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGKILL)
            process.wait()


def publish(results, partial=False):
    target = STATE if partial else OUTPUT
    target.mkdir(parents=True, exist_ok=True)
    totals = {status: sum(row['status'] == status for row in results)
              for status in ['MATCH', 'ACCEPTED_DIFFERENCE', 'MISMATCH']}
    payload = {'generatedAt': datetime.now(timezone.utc).isoformat(), 'totals': totals, 'cases': results}
    (target / 'results.json').write_text(json.dumps(payload, indent=2, default=str) + '\n')
    report = ['# PostgreSQL old-vs-new equivalence', '',
              f"MATCH: {totals['MATCH']}/{len(results)}; accepted differences: {totals['ACCEPTED_DIFFERENCE']}; unresolved: {totals['MISMATCH']}.",
              '', '| Case | Flow | Service | Result |', '|---|---|---|---|']
    report += [f"| {r['id']} | {r['flow']} | `{r['service']}` | {r['status']} |" for r in results]
    report += ['', '## Accepted differences', '']
    report += [f"- **{r['id']}**: {r['acceptedReason']}" for r in results if 'acceptedReason' in r]
    if not any('acceptedReason' in r for r in results):
        report += ['None. No business-value or row-count difference is accepted for these cases.']
    report += ['', '## Mismatch → cause → action history', '',
               'See [history.md](history.md) for investigations and fixes. Raw responses, row changes,',
               'assertion failures and exact differences are retained in [results.json](results.json).', '',
               '## Rerun', '', '```sh', 'bash migration/equivalence/run.sh', '```', '',
               'See [the harness README](../../../migration/equivalence/README.md) for prerequisites,',
               'schema/transaction boundaries and the precise normalization policy.',
               'See [unported.md](unported.md) for excluded original branches. Matching these cases',
               'does not certify the complete original applications.', '']
    (target / 'report.md').write_text('\n'.join(report))
    print(json.dumps(totals))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--reuse-template', action='store_true')
    parser.add_argument('--skip-build', action='store_true')
    parser.add_argument('--until', type=int)
    args = parser.parse_args()
    os.environ.setdefault('EQUIV_PASSWORD', secrets.token_hex(24))
    os.environ.setdefault('EQUIV_STATE', str(STATE))
    os.environ.setdefault('EQUIV_PG_PORT', '15432')
    os.environ['MIGRATION_DEMO_PASSWORD'] = os.environ['EQUIV_PASSWORD']
    os.environ['MANUFACTURING_DEMO_PASSWORD'] = os.environ['EQUIV_PASSWORD']
    if args.reuse_template:
        info = json.loads(command('docker', 'inspect', CONTAINER, capture_output=True).stdout)[0]
        if (info['Config'].get('Labels') or {}).get('org.ofbiz.equivalence') != 'true':
            raise RuntimeError('Refusing to alter an unlabelled PostgreSQL container')
        for database in ['equiv_old', 'equiv_new']:
            sql('postgres', f'DROP DATABASE IF EXISTS {database} WITH (FORCE)')
            sql('postgres', f'CREATE DATABASE {database} TEMPLATE equiv_template OWNER ofbiz')
    else:
        setup()
    if not args.skip_build:
        with (STATE / 'build-apps.log').open('w') as log:
            command(str(ROOT / 'gradlew'), '--max-workers=4', ':migration:equivalence:bootJar',
                    ':migration:humanres-springboot:bootJar', cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
    manifest = json.loads((HERE / 'cases.json').read_text())
    processes = start_servers()
    try:
        results = run_cases(manifest, args.until)
        publish(results, partial=args.until is not None)
    finally:
        stop_servers(processes)


if __name__ == '__main__':
    main()
