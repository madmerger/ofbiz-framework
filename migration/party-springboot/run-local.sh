#!/usr/bin/env bash
set -euo pipefail

: "${PARTY_DEMO_PASSWORD:?Set PARTY_DEMO_PASSWORD before running the local pilot}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
if [[ ! -x "$REPO_ROOT/gradlew" ]]; then
  echo "Gradle wrapper was not found at $REPO_ROOT/gradlew." >&2
  exit 1
fi

exec "$REPO_ROOT/gradlew" -p "$REPO_ROOT" :migration:party-springboot:bootRun \
  --args='--spring.profiles.active=local'
