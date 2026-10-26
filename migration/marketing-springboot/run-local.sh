#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${MIGRATION_DEMO_PASSWORD:-}" ]]; then
  echo "MIGRATION_DEMO_PASSWORD を設定してください（デモユーザー共通パスワード）" >&2
  exit 1
fi

cd "$(dirname "$0")/../.."
exec ./gradlew :migration:marketing-springboot:bootRun --args='--spring.profiles.active=local'
