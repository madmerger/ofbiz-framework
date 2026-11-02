#!/usr/bin/env python3
"""Provision only the labelled, loopback-only equivalence PostgreSQL container."""
import json
import os
from pathlib import Path
import shutil
import subprocess
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
STATE = Path(os.environ.get('EQUIV_STATE', Path.home() / '.local/state/ofbiz-equivalence')).resolve()
CONTAINER = 'ofbiz-equivalence-harness'
PORT = os.environ.get('EQUIV_PG_PORT', '15432')


def command(*args, **kwargs):
    return subprocess.run(args, check=True, text=True, **kwargs)


def sql(database, query):
    return command('docker', 'exec', CONTAINER, 'psql', '-X', '-v', 'ON_ERROR_STOP=1',
                   '-U', 'ofbiz', '-d', database, '-Atc', query, capture_output=True).stdout.strip()


def prepare():
    STATE.mkdir(parents=True, exist_ok=True)
    overlay = STATE / 'overlay'
    overlay.mkdir(exist_ok=True)
    shutil.copytree(ROOT / 'framework/catalina', STATE / 'catalina', dirs_exist_ok=True)
    path = STATE / 'catalina/ofbiz-component.xml'
    path.write_text(path.read_text().replace('<!--<property name="address" value=""/>-->',
                    '<property name="address" value="127.0.0.1"/>')
                    .replace('value="8443"', 'value="18443"').replace('value="8080"', 'value="18080"'))
    components = ET.parse(ROOT / 'framework/base/config/component-load.xml')
    root = components.getroot()
    for node in list(root):
        if node.get('parent-directory') == 'framework':
            position = list(root).index(node)
            root.remove(node)
            for child in ET.parse(ROOT / 'framework/component-load.xml').getroot():
                name = child.get('component-location')
                location = str(STATE / 'catalina') if name == 'catalina' else f'framework/{name}'
                root.insert(position, ET.Element('load-component', {'component-location': location}))
                position += 1
        elif node.get('parent-directory') == 'plugins':
            root.remove(node)
    root.append(ET.Element('load-component', {'component-location': 'migration/equivalence/plugin'}))
    components.write(overlay / 'component-load.xml', encoding='unicode')
    init = STATE / 'equivalence.gradle'
    init.write_text('''gradle.projectsEvaluated {
    def root = gradle.rootProject
    root.tasks.withType(JavaExec).configureEach {
        if (name.startsWith('ofbiz')) {
            doFirst {
                def overlay = new File(System.getenv('EQUIV_STATE'), 'overlay')
                def config = new File(root.projectDir, 'framework/entity/config/entityengine.xml').text
                config = config.replace('datasource-name="localh2"', 'datasource-name="localpostgres"')
                config = config.replace('jdbc:postgresql://127.0.0.1/ofbiz"',
                    'jdbc:postgresql://127.0.0.1:' + System.getenv('EQUIV_PG_PORT') + '/' + System.getenv('EQUIV_DB') + '"')
                config = config.replace('jdbc:h2:./runtime/data/', 'jdbc:h2:' + System.getenv('EQUIV_STATE') + '/h2/')
                new File(overlay, 'entityengine.xml').text = config
                def driver = root.project(':migration:party-springboot').configurations.runtimeClasspath.files.findAll {
                    it.name.startsWith('postgresql-')
                }
                classpath = root.files(overlay, driver) + classpath
                new File(System.getenv('EQUIV_STATE'), 'old-classpath.txt').text = classpath.asPath
                new File(System.getenv('EQUIV_STATE'), 'old-jvm-args.json').text = groovy.json.JsonOutput.toJson(jvmArgs)
            }
        }
    }
}
''')
    return init


def setup():
    init = prepare()
    found = subprocess.run(['docker', 'inspect', CONTAINER], capture_output=True, text=True)
    if found.returncode:
        command('docker', 'run', '-d', '--name', CONTAINER, '--label', 'org.ofbiz.equivalence=true',
                '-p', f'127.0.0.1:{PORT}:5432', '-e', 'POSTGRES_USER=ofbiz',
                '-e', 'POSTGRES_HOST_AUTH_METHOD=trust', 'postgres:16-alpine')
    else:
        info = json.loads(found.stdout)[0]
        if info['Config']['Labels'].get('org.ofbiz.equivalence') != 'true':
            raise RuntimeError('Refusing to alter an unlabelled PostgreSQL container')
        command('docker', 'start', CONTAINER, stdout=subprocess.DEVNULL)
    for _ in range(60):
        ready = subprocess.run(['docker', 'exec', CONTAINER, 'pg_isready', '-U', 'ofbiz'], capture_output=True)
        if ready.returncode == 0:
            break
        time.sleep(1)
    else:
        raise RuntimeError('PostgreSQL did not become ready')
    for database in ['equiv_old', 'equiv_new', 'equiv_template']:
        sql('postgres', f'DROP DATABASE IF EXISTS {database} WITH (FORCE)')
    sql('postgres', 'CREATE DATABASE equiv_template OWNER ofbiz')
    env = dict(os.environ, EQUIV_STATE=str(STATE), EQUIV_PG_PORT=PORT, EQUIV_DB='equiv_template')
    with (STATE / 'load-template.log').open('w') as log:
        command(str(ROOT / 'gradlew'), '--max-workers=4', '-I', str(init), '-PskipNpmInstall',
                '-x', ':framework:rest-api:prepareDocAssets', 'ofbiz --load-data',
                cwd=ROOT, env=env, stdout=log, stderr=subprocess.STDOUT)
    tables = int(sql('equiv_template', "select count(*) from information_schema.tables where table_schema='public'"))
    if tables < 800:
        raise RuntimeError(f'Incomplete original schema: {tables} tables; inspect load-template.log')
    for table in ['party', 'product', 'user_login']:
        if int(sql('equiv_template', f'select count(*) from {table}')) == 0:
            raise RuntimeError(f'Missing original demo data: {table}')
    for database in ['equiv_old', 'equiv_new']:
        sql('postgres', f'CREATE DATABASE {database} TEMPLATE equiv_template OWNER ofbiz')
    config = STATE / 'overlay/entityengine.xml'
    config.write_text(config.read_text().replace('/equiv_template"', '/equiv_old"'))
    print(f'Original seed/demo template: {tables} tables; cloned to equiv_old and equiv_new')


if __name__ == '__main__':
    setup()
