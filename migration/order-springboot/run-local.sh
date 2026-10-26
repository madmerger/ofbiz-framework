#!/usr/bin/env bash
set -euo pipefail

: "${MIGRATION_DEMO_PASSWORD:?MIGRATION_DEMO_PASSWORD を設定してください}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
if [[ -z "${GRADLE_BIN:-}" ]]; then
  GRADLE_BIN="$(command -v gradle || true)"
fi
if [[ -z "${GRADLE_BIN:-}" && -x "$HOME/.gradle/gradle-9.8.2/bin/gradle" ]]; then
  GRADLE_BIN="$HOME/.gradle/gradle-9.8.2/bin/gradle"
fi
if [[ -z "${GRADLE_BIN:-}" || ! -x "$GRADLE_BIN" ]]; then
  echo "Gradle 9.8.2 が見つかりません。実行可能な Gradle を GRADLE_BIN に指定してください。" >&2
  exit 1
fi

exec "$GRADLE_BIN" -p "$REPO_ROOT" :migration:order-springboot:bootRun \
  --args='--spring.profiles.active=local'
