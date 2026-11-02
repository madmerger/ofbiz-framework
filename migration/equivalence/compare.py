"""Conservative normalization: business values and multiplicity remain observable."""
from collections import Counter
from decimal import Decimal
import json
import re

GENERATED_TIMES = {
    'created_stamp', 'created_tx_stamp', 'last_updated_stamp', 'last_updated_tx_stamp',
    'from_date', 'change_date', 'status_datetime', 'status_date', 'posted_date',
    'order_date', 'entry_date', 'created_date', 'last_modified_date', 'invoice_date',
    'effective_date', 'paid_date', 'last_modified_date_time',
    'last_status_update', 'timestamp',
}
ALIASES = {
    'party_id': ['party_id_from', 'party_id_to', 'organization_party_id', 'owner_party_id',
                 'bill_to_party_id', 'bill_from_party_id', 'ship_to_party_id', 'ship_from_party_id'],
    'contact_mech_id': ['origin_contact_mech_id', 'destination_contact_mech_id'],
    'work_effort_id': ['estimated_ship_work_eff_id', 'estimated_arrival_work_eff_id', 'parent_work_effort_id'],
    'order_id': ['primary_order_id'],
}


def snake(name):
    return re.sub(r'(?<!^)(?=[A-Z])', '_', name).lower()


def encoded(value):
    return json.dumps(value, sort_keys=True, separators=(',', ':'), default=str)


class Normalizer:
    def __init__(self):
        self.ids = {'old': {}, 'new': {}}

    def bind(self, field, old, new, label):
        domain = snake(field)
        for side, value in [('old', old), ('new', new)]:
            if value is None:
                continue
            for alias in [domain, *ALIASES.get(domain, [])]:
                self.ids[side].setdefault((alias, str(value)), '$' + label)

    def normalize(self, value, side, field=''):
        field = snake(field)
        if isinstance(value, dict):
            return {key: self.normalize(item, side, key) for key, item in sorted(value.items())}
        if isinstance(value, list):
            return sorted([self.normalize(item, side, field) for item in value], key=encoded)
        if value is None:
            return None
        if (field, str(value)) in self.ids[side]:
            return self.ids[side][(field, str(value))]
        if field in GENERATED_TIMES:
            return '<timestamp-present>'
        if isinstance(value, (Decimal, int, float)) and not isinstance(value, bool):
            return format(Decimal(str(value)).normalize(), 'f')
        return value


def difference(before, after):
    left = Counter(map(encoded, before))
    right = Counter(map(encoded, after))
    return {'removed': [json.loads(row) for row in sorted((left - right).elements())],
            'added': [json.loads(row) for row in sorted((right - left).elements())]}


def bind_created_keys(normalizer, step, table, fields, before, after):
    created = {}
    for side in ['old', 'new']:
        existing = {tuple(str(row.get(field)) for field in fields) for row in before[side][table]}
        created[side] = [row for row in after[side][table]
                         if tuple(str(row.get(field)) for field in fields) not in existing]
    if len(created['old']) == len(created['new']) == 1:
        pairs = [(created['old'][0], created['new'][0])]
    else:
        by_signature = {'old': {}, 'new': {}}
        for side in ['old', 'new']:
            for row in created[side]:
                signature = encoded(normalizer.normalize({k: v for k, v in row.items() if k not in fields}, side))
                by_signature[side].setdefault(signature, []).append(row)
        pairs = [(rows[0], by_signature['new'][signature][0])
                 for signature, rows in by_signature['old'].items()
                 if len(rows) == 1 and len(by_signature['new'].get(signature, [])) == 1]
    for index, (old, new) in enumerate(pairs):
        for field in fields:
            normalizer.bind(field, old.get(field), new.get(field), f'{step}.{table}.{index}.{field}')


def delta(before, after, normalizer, side):
    return {table: difference(normalizer.normalize(before[table], side),
                              normalizer.normalize(after[table], side)) for table in before}


def mismatches(left, right, path=''):
    if isinstance(left, dict) and isinstance(right, dict):
        result = []
        for key in sorted(left.keys() | right.keys()):
            location = path + '/' + key
            if key not in left or key not in right:
                result.append({'path': location, 'old': left.get(key), 'new': right.get(key),
                               'missing': 'old' if key not in left else 'new'})
            else:
                result.extend(mismatches(left[key], right[key], location))
        return result
    return [] if left == right else [{'path': path, 'old': left, 'new': right}]


def response_contract(responses, parameters, success):
    messages = {'responseMessage', 'successMessage', 'successMessageList',
                'errorMessage', 'errorMessageList', 'errorMessageMap'}
    categories = {'errorCode', 'errorCategory'}
    outputs = {key for key, mode in parameters.items() if mode in ('OUT', 'INOUT')} - messages - categories
    shared_categories = {key for key in categories if all(key in responses[side] for side in ('old', 'new'))}
    compared = {}
    info = {}
    for side in ('old', 'new'):
        compared[side] = {key: value for key, value in responses[side].items()
                          if key in outputs or key in shared_categories}
        compared[side]['classification'] = 'success' if success[side] else 'error'
        info[side] = {key: value for key, value in responses[side].items()
                      if key not in outputs and key not in shared_categories and key != 'responseMessage'}
    return compared, info
