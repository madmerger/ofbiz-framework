#!/usr/bin/env python3
"""Generate the Japanese inventory of Order functionality not ported to Spring Boot."""

from pathlib import Path
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[3]
ORDER = ROOT / "applications/order"
MIGRATED = {
    "storeOrder",
    "changeOrderStatus",
    "changeOrderItemStatus",
    "cancelOrderItem",
    "cancelOrderItemNoActions",
    "orderAdjustmentPermissionCheck",
    "updateOrderStatusFromReceipt",
    "returnPermissionCheck",
    "custRequestPermissionCheck",
    "createCustRequest",
    "updateCustRequest",
    "deleteCustRequest",
    "copyCustRequestItem",
    "checkStatusCustRequest",
    "updateCustRequestLastModifiedDate",
    "createCustRequestNote",
    "updateCustRequestNote",
    "createCustRequestItemNote",
    "createCustRequestFromCart",
    "createCustRequestFromShoppingList",
    "getCustRequestsByRole",
    "setCustRequestStatus",
    "createCustRequestFromCommEvent",
    "createRequirement",
}
REASONS = {
    "サービス": "主要フロー外（スコープ外）",
    "画面": "画面は API で代替、UI 再構築は別途",
    "テンプレート": "画面は API で代替、UI 再構築は別途",
    "view-entity": "照会用 view-entity は別途設計",
    "controller": "画面イベントは API で代替、UI 再構築は別途",
    "ECA": "ECA は実行せず、必要な副作用は移植サービスで明示的に処理",
    "minilang event": "画面イベントは API で代替、UI 再構築は別途",
    "Groovy screen action": "画面専用処理は UI 再構築時に移植",
}


def text_attribute(element, name):
    return element.get(name, "")


def local_name(element):
    return element.tag.rsplit("}", 1)[-1]


def service_rows():
    rows = []
    service_locations = set()
    for path in sorted((ORDER / "servicedef").glob("services*.xml")):
        root = ET.parse(path).getroot()
        for service in root.iter():
            if local_name(service) != "service":
                continue
            engine = text_attribute(service, "engine")
            name = text_attribute(service, "name")
            location = text_attribute(service, "location")
            invoke = text_attribute(service, "invoke")
            if engine in {"java", "groovy", "interface", "group"}:
                if location:
                    service_locations.add(location)
                if name not in MIGRATED:
                    rows.append((
                        "サービス", name, engine, location, invoke,
                        path.relative_to(ROOT).as_posix(), REASONS["サービス"],
                    ))
    return rows, service_locations


def definition_rows():
    rows = []
    widget_tags = {"screen", "form", "menu", "tree"}
    for path in sorted((ORDER / "widget").rglob("*.xml")):
        try:
            root = ET.parse(path).getroot()
        except ET.ParseError:
            continue
        for element in root.iter():
            tag = local_name(element)
            if tag in widget_tags:
                rows.append((
                    "画面", text_attribute(element, "name") or tag, tag, "",
                    "", path.relative_to(ROOT).as_posix(), REASONS["画面"],
                ))
    for base in (ORDER / "template", ORDER / "webapp"):
        if base.exists():
            for path in sorted(base.rglob("*.ftl")):
                rows.append((
                    "テンプレート", path.name, "ftl", "", "",
                    path.relative_to(ROOT).as_posix(), REASONS["テンプレート"],
                ))

    for path in [ORDER / "entitydef/entitymodel_view.xml"]:
        if not path.exists():
            continue
        root = ET.parse(path).getroot()
        for view in root.iter():
            if local_name(view) != "view-entity":
                continue
            rows.append((
                "view-entity", text_attribute(view, "entity-name"), "", "", "",
                path.relative_to(ROOT).as_posix(), REASONS["view-entity"],
            ))
    return rows


def controller_rows():
    path = ORDER / "webapp/ordermgr/WEB-INF/controller.xml"
    rows = []
    if not path.exists():
        return rows
    for request in ET.parse(path).getroot().iter():
        if local_name(request) != "request-map":
            continue
        for event in request:
            if local_name(event) != "event" or event.get("type") != "service":
                continue
            service = event.get("invoke", "")
            if service not in MIGRATED:
                rows.append((
                    "controller", request.get("uri", ""), "service", "", service,
                    path.relative_to(ROOT).as_posix(), REASONS["controller"],
                ))
    return rows


def eca_rows():
    rows = []
    for path in sorted(ORDER.rglob("secas.xml")) + sorted(ORDER.rglob("eecas.xml")):
        for element in ET.parse(path).getroot().iter():
            if local_name(element).lower() in {"eca", "seca", "eeca"}:
                name = " / ".join(filter(None, (
                    element.get("entity", ""), element.get("service", ""), element.get("operation", ""),
                )))
                rows.append((
                    "ECA", name or element.tag, element.tag, "", "",
                    path.relative_to(ROOT).as_posix(), REASONS["ECA"],
                ))
    return rows


def minilang_rows():
    rows = []
    minilang = ORDER / "minilang"
    selected = (
        minilang / "request/CustRequestEvents.xml",
        minilang / "customer/CustomerEvents.xml",
        minilang / "customer/CheckoutMapProcs.xml",
    )
    selected += tuple(sorted((minilang / "reports").glob("*.xml"))) if (minilang / "reports").exists() else ()
    for path in selected:
        if not path.exists():
            continue
        for element in ET.parse(path).getroot().iter():
            tag = local_name(element)
            if tag in {"simple-method", "event"}:
                name = element.get("method-name") or element.get("name") or tag
                rows.append((
                    "minilang event", name, tag, "", "",
                    path.relative_to(ROOT).as_posix(), REASONS["minilang event"],
                ))
    return rows


def groovy_action_rows(service_locations):
    rows = []
    source = ORDER / "src/main/groovy"
    if not source.exists():
        return rows
    locations = {location.replace("\\", "/") for location in service_locations}
    for path in sorted(source.rglob("*.groovy")):
        relative = path.relative_to(ORDER).as_posix()
        if relative in locations or path.name in {Path(location).name for location in locations}:
            continue
        rows.append((
            "Groovy screen action", path.stem, "groovy", relative, "",
            path.relative_to(ROOT).as_posix(), REASONS["Groovy screen action"],
        ))
    return rows


def main():
    rows, service_locations = service_rows()
    rows.extend(definition_rows())
    rows.extend(controller_rows())
    rows.extend(eca_rows())
    rows.extend(minilang_rows())
    rows.extend(groovy_action_rows(service_locations))
    rows.sort(key=lambda row: (row[0], row[1], row[5]))

    categories = {}
    for row in rows:
        categories[row[0]] = categories.get(row[0], 0) + 1
    lines = [
        "# Order Spring Boot 未移植棚卸し",
        "",
        "本ファイルは `migration/order-springboot/scripts/gen_unported.py` で生成。",
        "",
        "## 区分別件数",
        "",
        "| 区分 | 件数 |",
        "| --- | ---: |",
    ]
    lines.extend(f"| {category} | {count} |" for category, count in sorted(categories.items()))
    lines.extend([
        "",
        "## 一覧",
        "",
        "| 区分 | 名称 | 種別 | location | invoke | 定義ファイル | 理由 |",
        "| --- | --- | --- | --- | --- | --- | --- |",
    ])
    for category, name, kind, location, invoke, source, reason in rows:
        cells = (category, name, kind, location, invoke, source, reason)
        lines.append("| " + " | ".join(str(value).replace("|", "\\|").replace("\n", " ") for value in cells) + " |")
    output = ROOT / "docs/migration/order/unported.md"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"generated {output.relative_to(ROOT)} ({len(rows)} rows)")


if __name__ == "__main__":
    main()
