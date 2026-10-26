#!/usr/bin/env python3
#
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

"""Manufacturing の未移植一覧と機械集計を再生成する。"""

from collections import Counter
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[3]
COMPONENT = ROOT / "applications/manufacturing"
MIGRATED = {
    "searchDuplicatedAncestor",
    "getProductRouting",
    "getManufacturingComponents",
    "createProductionRun",
    "changeProductionRunStatus",
    "changeProductionRunTaskStatus",
    "updateProductionRunTask",
}


def local_name(element):
    return element.tag.rsplit("}", 1)[-1]


def elements(root, name):
    return [element for element in root.iter() if local_name(element) == name]


def reason(name, engine, source):
    if name == "manufacturingPermissionService":
        return "権限判定は common の MANUFACTURING main-action マッピングで担保する。"
    if "mrp" in source.lower() or "mrp" in name.lower():
        return "MRP 計画・イベント・実行ログは本移行の対象外。"
    if "production_run" in source:
        return "在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。"
    if "routing" in source:
        return "画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。"
    if "lookup" in source or "search" in name.lower() or "find" in name.lower():
        return "画面用検索・候補表示サービスは本移行対象外。"
    if "bom" in source:
        return "BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。"
    if engine == "java":
        return "設計で指定された主要 Java フロー以外は段階移行対象。"
    return "設計で指定された主要フロー以外は段階移行対象。"


def main():
    service_files = sorted((COMPONENT / "servicedef").glob("services*.xml"))
    services = []
    for path in service_files:
        root = ET.parse(path).getroot()
        for service in elements(root, "service"):
            services.append({
                "name": service.get("name", ""),
                "engine": service.get("engine", ""),
                "source": path.relative_to(ROOT).as_posix(),
                "location": service.get("location", ""),
            })
    counts = Counter(service["engine"] for service in services)
    unported = [
        service for service in services
        if service["engine"] in {"java", "groovy"} and service["name"] not in MIGRATED
    ]
    interfaces = [service for service in services if service["engine"] == "interface"]
    widgets = []
    screen_count = 0
    for path in sorted((COMPONENT / "widget/manufacturing").glob("*.xml")):
        root = ET.parse(path).getroot()
        kind = "screen" if "Screens" in path.name else "form" if "Forms" in path.name else "menu"
        screens = len(elements(root, "screen"))
        screen_count += screens
        widgets.append((path.relative_to(ROOT).as_posix(), kind, screens))
    controller = ET.parse(COMPONENT / "webapp/manufacturing/WEB-INF/controller.xml").getroot()
    request_count = len(elements(controller, "request-map"))
    view_count = len(elements(controller, "view-map"))
    template_count = len(list((COMPONENT / "template").rglob("*.ftl")))
    entity_model = ET.parse(ROOT / "applications/datamodel/entitydef/manufacturing-entitymodel.xml").getroot()
    view_entities = [item.get("entity-name", "") for item in elements(entity_model, "view-entity")]
    seca_root = ET.parse(COMPONENT / "servicedef/secas.xml").getroot()
    secas = elements(seca_root, "eca")

    lines = [
        "# 未移植項目と画面・サービス集計",
        "",
        "この文書は `python3 docs/migration/manufacturing/render_unported.py` で再生成する。",
        "",
        "## 件数集計",
        "",
        "| 項目 | 件数 |",
        "| --- | ---: |",
        f"| servicedef（XML） | {len(service_files)} |",
        f"| entity-auto | {counts['entity-auto']} |",
        f"| simple | {counts['simple']} |",
        f"| Java | {counts['java']} |",
        f"| Groovy | {counts['groovy']} |",
        f"| interface | {counts['interface']} |",
        f"| 全サービス | {len(services)} |",
        f"| 移植 Java | {sum(s['engine'] == 'java' and s['name'] in MIGRATED for s in services)} |",
        f"| 移植 Groovy | {sum(s['engine'] == 'groovy' and s['name'] in MIGRATED for s in services)} |",
        f"| 未移植 Java/Groovy | {len(unported)} |",
        f"| controller request-map | {request_count} |",
        f"| controller view-map | {view_count} |",
        f"| FTL | {template_count} |",
        f"| widget screen | {screen_count} |",
        f"| view-entity | {len(view_entities)} |",
        f"| 未再現 SECA | {len(secas)} |",
        "",
        "## 未移植 Java / Groovy サービス",
        "",
        "| サービス名 | engine | servicedef | location | 理由 |",
        "| --- | --- | --- | --- | --- |",
    ]
    for service in unported:
        why = reason(service["name"], service["engine"], service["source"])
        lines.append(
            f"| `{service['name']}` | {service['engine']} | `{service['source']}` "
            f"| `{service['location']}` | {why} |"
        )
    lines.extend(["", "## interface サービス", "", "| サービス名 | location |", "| --- | --- |"])
    lines.extend(f"| `{item['name']}` | `{item['location']}` |" for item in interfaces)
    lines.extend([
        "",
        "## Widget 定義ファイル",
        "",
        "| ファイル | 種別 | screen 数 |",
        "| --- | --- | ---: |",
    ])
    lines.extend(f"| `{path}` | {kind} | {count} |" for path, kind, count in widgets)
    lines.extend([
        "",
        "## view-entity",
        "",
        "| entity-name |",
        "| --- |",
    ])
    lines.extend(f"| `{name}` |" for name in view_entities)
    lines.extend([
        "",
        "## 未再現 SECA",
        "",
        "| service | event | action |",
        "| --- | --- | --- |",
    ])
    for seca in secas:
        actions = ", ".join(
            action.get("service", "") for action in elements(seca, "action")
        )
        lines.append(f"| `{seca.get('service', '')}` | {seca.get('event', '')} | `{actions}` |")
    (ROOT / "docs/migration/manufacturing/unported.md").write_text(
        "\n".join(lines) + "\n", encoding="utf-8"
    )
    print(
        f"services={len(services)} entity-auto={counts['entity-auto']} simple={counts['simple']} "
        f"java={counts['java']} groovy={counts['groovy']} interface={counts['interface']} "
        f"migrated-java=6 migrated-groovy=1 unported-java-groovy={len(unported)} "
        f"widgets={len(widgets)} screens={screen_count} requests={request_count} "
        f"views={view_count} ftl={template_count} view-entities={len(view_entities)} "
        f"secas={len(secas)}"
    )


if __name__ == "__main__":
    main()
