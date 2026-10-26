#!/usr/bin/env python3
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

"""Build a source inventory for an Apache OFBiz component."""

import argparse
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from xml.parsers import expat


TEXT_SUFFIXES = {
    ".ftl", ".ftlh", ".groovy", ".html", ".java", ".js", ".json", ".properties",
    ".txt", ".xml", ".xsd", ".yml", ".yaml",
}
CALL_PATTERN = re.compile(r"\b(?:runSync|runAsync|runSyncIgnore|runSyncMulti)\s*\(\s*(['\"])([^'\"]+)\1")
ENTITY_ATTRIBUTE_PATTERN = re.compile(r"\bentity-name\s*=\s*(['\"])([^'\"]+)\1")
ENTITY_METHOD_PATTERN = re.compile(
    r"\.(?:from|find|findOne|findList|findCount|makeValue|makePK|getModelEntity|"
    r"findByAnd|findByPrimaryKey)\s*\(\s*(['\"])([^'\"]+)\1"
)


def local_name(name):
    return name.rsplit("}", 1)[-1]


def parse_xml(path):
    """Parse XML with expat so each element retains its exact source line."""
    parser = expat.ParserCreate(namespace_separator="}")
    stack = []
    lines = {}
    root = None

    def start(name, attributes):
        nonlocal root
        element = ET.Element(
            local_name(name),
            {local_name(key): value for key, value in attributes.items()},
        )
        lines[id(element)] = parser.CurrentLineNumber
        if stack:
            stack[-1].append(element)
        else:
            root = element
        stack.append(element)

    def end(_name):
        stack.pop()

    def text(value):
        if not stack or not value.strip():
            return
        element = stack[-1]
        if len(element):
            child = element[-1]
            child.tail = (child.tail or "") + value
        else:
            element.text = (element.text or "") + value

    parser.StartElementHandler = start
    parser.EndElementHandler = end
    parser.CharacterDataHandler = text
    parser.Parse(Path(path).read_bytes(), True)
    return root, lines


def rel_path(root, path):
    return Path(path).resolve().relative_to(Path(root).resolve()).as_posix()


def java_name_to_db_name(name):
    return "".join(("_" if index and char.isupper() else "") + char.upper()
                   for index, char in enumerate(name))


def component_name(component):
    return Path(component.rstrip("/")).name


def component_id(component):
    parts = Path(component).parts
    return parts[-1] if parts else component


def component_for_path(path):
    parts = Path(path).parts
    if len(parts) >= 2 and parts[0] in {"applications", "framework", "plugins"}:
        return parts[1]
    return None


def xml_files_under(root, *patterns):
    paths = set()
    for pattern in patterns:
        paths.update(Path(root).glob(pattern))
    return sorted(path for path in paths if path.is_file())


def entity_model_files(root):
    models = []
    for base in ("applications", "framework"):
        directory = Path(root) / base
        if directory.exists():
            models.extend(directory.rglob("entitydef/*.xml"))
    return sorted(path for path in models if path.is_file())


def parse_entitymodel(root, path):
    xml, lines = parse_xml(path)
    entities = []
    views = []
    for element in xml.iter():
        if element.tag not in {"entity", "view-entity"}:
            continue
        is_view = element.tag == "view-entity"
        name = element.get("entity-name")
        if not name:
            continue
        primary_keys = [key.get("field") for key in element.findall("prim-key")]
        fields = []
        for field in element.findall("field"):
            fields.append({
                "name": field.get("name"),
                "type": field.get("type"),
                "column": field.get("col-name"),
                "pk": field.get("name") in primary_keys,
            })
        source = rel_path(root, path)
        if is_view:
            views.append({
                "name": name,
                "package": element.get("package-name"),
                "source": source,
                "line": lines.get(id(element)),
                "fields": fields,
                "member_entities": [
                    {
                        "alias": member.get("entity-alias"),
                        "entity": member.get("entity-name"),
                    }
                    for member in element.findall("member-entity")
                ],
                "view_links": [
                    {
                        "entity_alias": link.get("entity-alias"),
                        "rel_entity_alias": link.get("rel-entity-alias"),
                        "rel_optional": link.get("rel-optional"),
                        "key_maps": [
                            {
                                "field": key.get("field-name"),
                                "rel_field": key.get("rel-field-name"),
                            }
                            for key in link.findall("key-map")
                        ],
                    }
                    for link in element.findall("view-link")
                ],
            })
            continue

        relations = []
        for relation in element.findall("relation"):
            relations.append({
                "type": relation.get("type"),
                "rel_entity": relation.get("rel-entity-name"),
                "title": relation.get("title"),
                "key_maps": [
                    {
                        "field": key.get("field-name"),
                        "rel_field": key.get("rel-field-name"),
                    }
                    for key in relation.findall("key-map")
                ],
            })
        entities.append({
            "name": name,
            "package": element.get("package-name"),
            "table": java_name_to_db_name(name),
            "source": source,
            "line": lines.get(id(element)),
            "fields": fields,
            "relations": relations,
        })
    return entities, views


def parse_service_file(root, path, entity_fields):
    xml, lines = parse_xml(path)
    services = []
    for service in xml.iter("service"):
        name = service.get("name")
        if not name:
            continue
        permission = service.find("permission-service")
        params = {}
        entity_name = service.get("default-entity-name")
        children = list(service)
        for element in children:
            if element.tag == "auto-attributes":
                model = (element.get("include") or "").lower()
                for field_name, field in entity_fields.get(entity_name, {}).items():
                    if model == "all" or (model == "pk" and field["pk"]) or (
                            model == "nonpk" and not field["pk"]):
                        params[field_name] = {
                            "name": field_name,
                            "type": field.get("type"),
                            "mode": element.get("mode"),
                            "optional": element.get("optional"),
                        }
        for element in children:
            if element.tag == "attribute":
                field_name = element.get("name")
                if field_name:
                    params[field_name] = {
                        "name": field_name,
                        "type": element.get("type"),
                        "mode": element.get("mode"),
                        "optional": element.get("optional"),
                    }
        for element in children:
            if element.tag == "override":
                field_name = element.get("name")
                if not field_name:
                    continue
                param = params.setdefault(field_name, {
                    "name": field_name,
                    "type": None,
                    "mode": None,
                    "optional": None,
                })
                for key in ("type", "mode", "optional"):
                    if key in element.attrib:
                        param[key] = element.get(key)
        services.append({
            "name": name,
            "source": rel_path(root, path),
            "line": lines.get(id(service)),
            "engine": service.get("engine"),
            "location": service.get("location"),
            "invoke": service.get("invoke"),
            "default_entity_name": entity_name,
            "auth": service.get("auth"),
            "use_transaction": service.get("use-transaction"),
            "transaction_timeout": service.get("transaction-timeout"),
            "permission_service": {
                "name": permission.get("service-name"),
                "main_action": permission.get("main-action"),
            } if permission is not None else None,
            "implements": [
                dict(item.attrib) for item in children if item.tag == "implements"
            ],
            "params": sorted(params.values(), key=lambda value: value["name"]),
        })
    return services


def parse_controller(root, path):
    xml, lines = parse_xml(path)
    view_maps = {}
    for view in xml.iter("view-map"):
        if view.get("name"):
            view_maps[view.get("name")] = {
                "name": view.get("name"),
                "type": view.get("type"),
                "page": view.get("page"),
                "source": rel_path(root, path),
                "line": lines.get(id(view)),
            }
    requests = []
    includes = []
    for include in xml.iter("include"):
        if include.get("location"):
            includes.append({
                "location": include.get("location"),
                "source": rel_path(root, path),
                "line": lines.get(id(include)),
            })
    for request in xml.iter("request-map"):
        responses = []
        for response in request.findall("response"):
            response_type = response.get("type")
            response_value = response.get("value")
            view = view_maps.get(response_value) if response_type == "view" else None
            responses.append({
                "name": response.get("name"),
                "type": response_type,
                "value": response_value,
                "page": view.get("page") if view else None,
            })
        security = request.find("security")
        event = request.find("event")
        requests.append({
            "uri": request.get("uri"),
            "source": rel_path(root, path),
            "line": lines.get(id(request)),
            "security": {
                "https": security.get("https"),
                "auth": security.get("auth"),
            } if security is not None else None,
            "event": {
                "type": event.get("type"),
                "path": event.get("path"),
                "invoke": event.get("invoke"),
            } if event is not None else None,
            "responses": responses,
        })
    return requests, list(view_maps.values()), includes


def service_definitions(root):
    definitions = {}
    paths = []
    for base in ("applications", "framework", "plugins"):
        directory = Path(root) / base
        if directory.exists():
            paths.extend(directory.rglob("servicedef/*.xml"))
    for path in sorted(set(paths)):
        if not path.is_file():
            continue
        xml, _lines = parse_xml(path)
        owner = component_for_path(rel_path(root, path))
        for service in xml.iter("service"):
            name = service.get("name")
            if name:
                definitions.setdefault(name, []).append({
                    "component": owner,
                    "source": rel_path(root, path),
                })
    return definitions


def resolve_service(name, definitions):
    providers = definitions.get(name, [])
    return {
        "components": sorted({provider["component"] for provider in providers if provider["component"]}),
        "definition_files": sorted({provider["source"] for provider in providers}),
    }


def source_files(component_path):
    if not component_path.exists():
        return []
    return sorted(
        path for path in component_path.rglob("*")
        if path.is_file() and path.suffix.lower() in TEXT_SUFFIXES
    )


def find_dependencies(root, component, component_path, own_entity_names, entity_owners, definitions):
    component_key = component_id(component)
    calls = []
    external_entities = []
    for path in source_files(component_path):
        relative = rel_path(root, path)
        if path.suffix.lower() == ".xml":
            try:
                xml, lines = parse_xml(path)
            except expat.ExpatError:
                xml = None
                lines = {}
            if xml is not None and "minilang" in path.parts:
                for element in xml.iter():
                    if element.tag == "call-service":
                        name = element.get("service-name")
                        if name:
                            owner = resolve_service(name, definitions)
                            calls.append({
                                "kind": "call-service",
                                "name": name,
                                "source": relative,
                                "line": lines.get(id(element)),
                                **owner,
                            })
                    elif element.tag == "call-simple-method":
                        method = element.get("method-name")
                        if method:
                            resource = element.get("xml-resource", "")
                            owner_match = re.match(r"component://([^/]+)/", resource)
                            owner = owner_match.group(1) if owner_match else None
                            calls.append({
                                "kind": "call-simple-method",
                                "name": method,
                                "source": relative,
                                "line": lines.get(id(element)),
                                "components": [owner] if owner else [],
                                "definition_files": [resource] if resource else [],
                            })

        if path.suffix.lower() in {".java", ".groovy", ".xml"}:
            try:
                text = path.read_text(encoding="utf-8")
            except (OSError, UnicodeDecodeError):
                continue
            if path.suffix.lower() in {".java", ".groovy"}:
                for match in CALL_PATTERN.finditer(text):
                    name = match.group(2)
                    calls.append({
                        "kind": match.group(0).split("(", 1)[0].strip(),
                        "name": name,
                        "source": relative,
                        "line": text.count("\n", 0, match.start()) + 1,
                        **resolve_service(name, definitions),
                    })
            if path.suffix.lower() in {".java", ".groovy", ".xml"} and (
                    "minilang" in path.parts or "widget" in path.parts or
                    path.suffix.lower() in {".java", ".groovy"}):
                matches = []
                matches.extend((match, match.group(2)) for match in ENTITY_ATTRIBUTE_PATTERN.finditer(text))
                matches.extend((match, match.group(2)) for match in ENTITY_METHOD_PATTERN.finditer(text))
                for match, name in matches:
                    if name not in own_entity_names:
                        owners = entity_owners.get(name, [])
                        external_entities.append({
                            "name": name,
                            "source": relative,
                            "line": text.count("\n", 0, match.start()) + 1,
                            "owning_entitymodels": sorted({owner["source"] for owner in owners}),
                            "owner_components": sorted({owner["component"] for owner in owners if owner["component"]}),
                        })

    reverse_dependencies = []
    marker = f"component://{component_key}/"
    for base in ("applications", "framework", "plugins"):
        directory = Path(root) / base
        if not directory.exists():
            continue
        for path in sorted(directory.rglob("*")):
            if (not path.is_file() or path.suffix.lower() not in TEXT_SUFFIXES or
                    component_for_path(rel_path(root, path)) == component_key):
                continue
            try:
                content = path.read_text(encoding="utf-8")
            except (OSError, UnicodeDecodeError):
                continue
            for line_number, line in enumerate(content.splitlines(), 1):
                if marker in line:
                    reverse_dependencies.append({
                        "source": rel_path(root, path),
                        "line": line_number,
                        "component": component_for_path(rel_path(root, path)),
                        "references": line.count(marker),
                    })
    calls.sort(key=lambda call: (call["source"], call["line"] or 0, call["kind"], call["name"]))
    external_entities.sort(key=lambda item: (item["name"], item["source"], item["line"]))
    reverse_dependencies.sort(key=lambda item: (item["source"], item["line"]))
    return {
        "called_services": calls,
        "external_entity_references": external_entities,
        "reverse_dependencies": reverse_dependencies,
    }


def build_inventory(root, component, entitymodel):
    root = Path(root).resolve()
    component_path = (root / component).resolve()
    model_path = (root / entitymodel).resolve()
    if not component_path.is_dir():
        raise FileNotFoundError(f"component directory does not exist: {component}")
    if not model_path.is_file():
        raise FileNotFoundError(f"entitymodel does not exist: {entitymodel}")

    all_entities = []
    all_views = []
    entity_owners = {}
    for path in entity_model_files(root):
        entities, views = parse_entitymodel(root, path)
        owner = component_for_path(rel_path(root, path))
        for entity in entities:
            entity_owners.setdefault(entity["name"], []).append({
                "source": entity["source"],
                "component": owner,
            })
        for view in views:
            entity_owners.setdefault(view["name"], []).append({
                "source": view["source"],
                "component": owner,
            })
        all_entities.extend(entities)
        all_views.extend(views)

    model_entities, model_views = parse_entitymodel(root, model_path)
    own_entity_names = {entity["name"] for entity in model_entities}
    own_entity_names.update(view["name"] for view in model_views)
    for entity in model_entities:
        for relation in entity["relations"]:
            owners = entity_owners.get(relation["rel_entity"], [])
            relation["external"] = relation["rel_entity"] not in own_entity_names
            relation["owning_entitymodels"] = sorted({owner["source"] for owner in owners})
            relation["owner_components"] = sorted({
                owner["component"] for owner in owners if owner["component"]
            })

    global_field_index = {}
    for entity in all_entities + all_views:
        global_field_index.setdefault(entity["name"], {})
        for field in entity["fields"]:
            global_field_index[entity["name"]][field["name"]] = field

    global_services = service_definitions(root)
    service_paths = sorted((component_path / "servicedef").rglob("*.xml"))
    services = []
    for path in service_paths:
        services.extend(parse_service_file(root, path, global_field_index))

    controller_paths = sorted((component_path / "webapp").rglob("WEB-INF/controller.xml"))
    requests = []
    view_maps = []
    includes = []
    for path in controller_paths:
        parsed_requests, parsed_views, parsed_includes = parse_controller(root, path)
        requests.extend(parsed_requests)
        view_maps.extend(parsed_views)
        includes.extend(parsed_includes)

    dependencies = find_dependencies(
        root, component, component_path, own_entity_names, entity_owners, global_services,
    )
    return {
        "component": component,
        "entitymodel": rel_path(root, model_path),
        "entitymodels_scanned": [rel_path(root, path) for path in entity_model_files(root)],
        "counts": {
            "entities": len(model_entities),
            "view_entities": len(model_views),
            "services": len(services),
            "entity_auto_services": sum(service["engine"] == "entity-auto" for service in services),
            "requests": len(requests),
            "view_maps": len(view_maps),
        },
        "entities": model_entities,
        "view_entities": model_views,
        "services": sorted(services, key=lambda service: service["name"]),
        "requests": sorted(requests, key=lambda request: request["uri"] or ""),
        "view_maps": sorted(view_maps, key=lambda view: view["name"]),
        "included_controllers": sorted(includes, key=lambda include: (include["source"], include["line"] or 0)),
        "dependencies": dependencies,
    }


def value(value):
    return "—" if value in (None, "") else str(value)


def markdown_table(headers, rows):
    output = ["| " + " | ".join(headers) + " |", "| " + " | ".join("---" for _ in headers) + " |"]
    for row in rows:
        output.append("| " + " | ".join(str(item).replace("|", "\\|").replace("\n", " ") for item in row) + " |")
    return "\n".join(output)


def entities_markdown(inventory):
    sections = ["# Entity inventory", ""]
    for entity in inventory["entities"]:
        sections.extend([
            f"## {entity['name']}",
            "",
            f"- Package: {value(entity['package'])}",
            f"- Table: `{entity['table']}`",
            f"- Source: `{entity['source']}:{entity['line']}`",
            "",
            "### Fields",
            "",
            markdown_table(
                ["Name", "Type", "Column", "PK"],
                [[field["name"], value(field["type"]), value(field["column"]), "Y" if field["pk"] else "N"]
                 for field in entity["fields"]],
            ),
            "",
            "### Relations",
            "",
            markdown_table(
                ["Type", "Related entity", "Title", "Key maps", "External owner"],
                [[
                    value(relation["type"]),
                    relation["rel_entity"] + (" (external)" if relation["external"] else ""),
                    value(relation["title"]),
                    ", ".join(f"{item['field']} → {value(item['rel_field'])}" for item in relation["key_maps"]) or "—",
                    (", ".join(relation["owning_entitymodels"]) or "unresolved") if relation["external"] else "—",
                ] for relation in entity["relations"]],
            ),
            "",
        ])
    sections.extend(["## View entities", ""])
    for view in inventory["view_entities"]:
        members = ", ".join(f"{item['alias']} = {item['entity']}" for item in view["member_entities"]) or "—"
        sections.extend([
            f"### {view['name']}",
            "",
            f"- Package: {value(view['package'])}",
            f"- Source: `{view['source']}:{view['line']}`",
            f"- Member entities: {members}",
            "",
            markdown_table(
                ["Entity alias", "Related alias", "Key maps"],
                [[
                    link["entity_alias"],
                    link["rel_entity_alias"],
                    ", ".join(f"{item['field']} → {value(item['rel_field'])}" for item in link["key_maps"]) or "—",
                ] for link in view["view_links"]],
            ),
            "",
        ])
    return "\n".join(sections)


def services_markdown(inventory):
    sections = ["# Service inventory", ""]
    for service in inventory["services"]:
        permission = service["permission_service"] or {}
        sections.extend([
            f"## {service['name']}",
            "",
            f"- Definition: `{service['source']}:{service['line']}`",
            f"- Engine: {value(service['engine'])}; location: {value(service['location'])}; invoke: {value(service['invoke'])}",
            f"- Default entity: {value(service['default_entity_name'])}; auth: {value(service['auth'])}",
            f"- Use transaction: {value(service['use_transaction'])}; timeout: {value(service['transaction_timeout'])}",
            f"- Permission service: {value(permission.get('name'))}; main action: {value(permission.get('main_action'))}",
            f"- Implements: {json.dumps(service['implements'], ensure_ascii=False) if service['implements'] else '—'}",
            "",
            markdown_table(
                ["Name", "Type", "Mode", "Optional"],
                [[param["name"], value(param["type"]), value(param["mode"]), value(param["optional"])]
                 for param in service["params"]],
            ),
            "",
        ])
    return "\n".join(sections)


def requests_markdown(inventory):
    sections = ["# Request inventory", "", "## Included controllers", ""]
    if inventory["included_controllers"]:
        sections.append(markdown_table(
            ["Source", "Line", "Location"],
            [[item["source"], item["line"], item["location"]]
             for item in inventory["included_controllers"]],
        ))
    else:
        sections.append("None.")
    sections.extend(["", "## View maps", ""])
    sections.append(markdown_table(
        ["Name", "Type", "Page", "Source", "Line"],
        [[view["name"], value(view["type"]), value(view["page"]), view["source"], view["line"]]
         for view in inventory["view_maps"]],
    ))
    sections.extend(["", "## Request maps", ""])
    for request in inventory["requests"]:
        security = request["security"] or {}
        event = request["event"] or {}
        response_text = "; ".join(
            f"{response['name']} → {response['type']}:{response['value']}"
            + (f" → {response['page']}" if response["page"] else "")
            for response in request["responses"]
        ) or "—"
        sections.extend([
            f"### {request['uri']}",
            "",
            f"- Source: `{request['source']}:{request['line']}`",
            f"- Security: HTTPS={value(security.get('https'))}, auth={value(security.get('auth'))}",
            f"- Event: type={value(event.get('type'))}, path={value(event.get('path'))}, invoke={value(event.get('invoke'))}",
            f"- Responses: {response_text}",
            "",
        ])
    return "\n".join(sections)


def dependencies_markdown(inventory):
    dependencies = inventory["dependencies"]
    sections = ["# Dependency inventory", "", "## Called services and simple methods", ""]
    sections.append(markdown_table(
        ["Kind", "Name", "Defined by", "Definition", "Source", "Line"],
        [[
            call["kind"], call["name"], ", ".join(call["components"]) or "unresolved",
            ", ".join(call["definition_files"]) or "—", call["source"], value(call["line"]),
        ] for call in dependencies["called_services"]],
    ))
    sections.extend(["", "## External entity references", ""])
    sections.append(markdown_table(
        ["Entity", "Owning component", "Owning entitymodel", "Source", "Line"],
        [[
            item["name"], ", ".join(item["owner_components"]) or "unresolved",
            ", ".join(item["owning_entitymodels"]) or "unresolved", item["source"], item["line"],
        ] for item in dependencies["external_entity_references"]],
    ))
    sections.extend(["", "## Reverse component references", ""])
    sections.append(markdown_table(
        ["Component", "File", "Line", "References"],
        [[item["component"] or "—", item["source"], item["line"], item["references"]]
         for item in dependencies["reverse_dependencies"]],
    ))
    return "\n".join(sections)


def write_outputs(inventory, output):
    output = Path(output)
    output.mkdir(parents=True, exist_ok=True)
    files = {
        "inventory.json": json.dumps(inventory, indent=2, ensure_ascii=False) + "\n",
        "entities.md": entities_markdown(inventory),
        "services.md": services_markdown(inventory),
        "requests.md": requests_markdown(inventory),
        "dependencies.md": dependencies_markdown(inventory),
    }
    for filename, content in files.items():
        (output / filename).write_text(content, encoding="utf-8")
    return sorted(output / filename for filename in files)


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--component", required=True, help="component directory relative to the OFBiz root")
    parser.add_argument("--entitymodel", required=True, help="entitymodel XML path relative to the OFBiz root")
    parser.add_argument("--out", required=True, help="output directory")
    parser.add_argument("--root", default=None, help="OFBiz repository root (defaults to current directory)")
    args = parser.parse_args(argv)
    root = Path(args.root).resolve() if args.root else Path.cwd().resolve()
    try:
        inventory = build_inventory(root, args.component, args.entitymodel)
        outputs = write_outputs(inventory, root / args.out)
    except (FileNotFoundError, expat.ExpatError, OSError, ValueError) as error:
        parser.error(str(error))
    for path in outputs:
        print(rel_path(root, path))
    print(json.dumps(inventory["counts"], sort_keys=True))
    return 0


if __name__ == "__main__":
    sys.exit(main())
