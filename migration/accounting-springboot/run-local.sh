#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${MIGRATION_DEMO_PASSWORD:-}" ]]; then
    echo "MIGRATION_DEMO_PASSWORD must be set" >&2
    exit 1
fi

./gradlew :migration:accounting-springboot:bootRun --args='--spring.profiles.active=local'
