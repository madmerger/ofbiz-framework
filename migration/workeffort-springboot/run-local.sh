#!/usr/bin/env bash
set -euo pipefail

: "${MIGRATION_DEMO_PASSWORD:?Set MIGRATION_DEMO_PASSWORD before running the local pilot}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
if [[ -n "${GRADLE_BIN:-}" ]]; then
  exec "$GRADLE_BIN" -p "$REPO_ROOT" :migration:workeffort-springboot:bootRun \
    --args='--spring.profiles.active=local'
fi

exec "$REPO_ROOT/gradlew" -p "$REPO_ROOT" :migration:workeffort-springboot:bootRun \
  --args='--spring.profiles.active=local'
