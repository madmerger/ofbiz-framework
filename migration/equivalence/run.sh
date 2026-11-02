#!/usr/bin/env bash
set -euo pipefail
export PYTHONDONTWRITEBYTECODE=1
python3 -u "$(dirname "$0")/harness.py" "$@"
