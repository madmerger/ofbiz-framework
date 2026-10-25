#!/usr/bin/env bash
set -euo pipefail

: "${HUMANRES_DEMO_PASSWORD:?Set HUMANRES_DEMO_PASSWORD before running the local pilot}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
if [[ -z "${GRADLE_BIN:-}" ]]; then
  GRADLE_BIN="$(command -v gradle || true)"
fi
if [[ -z "${GRADLE_BIN:-}" && -x "$HOME/.gradle/gradle-9.8.2/bin/gradle" ]]; then
  GRADLE_BIN="$HOME/.gradle/gradle-9.8.2/bin/gradle"
fi
if [[ -z "${GRADLE_BIN:-}" || ! -x "$GRADLE_BIN" ]]; then
  echo "Gradle 9.8.2 was not found; set GRADLE_BIN to an executable Gradle binary." >&2
  exit 1
fi

exec "$GRADLE_BIN" -p "$REPO_ROOT" :migration:humanres-springboot:bootRun \
  --args='--spring.profiles.active=local'
