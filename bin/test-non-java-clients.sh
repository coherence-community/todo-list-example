#!/usr/bin/env bash

# Copyright (c) 2026, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

# Install js/package.json and py/requirements.txt dependencies first.
# The supplied Coherence gRPC server must have a disposable empty task map.
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 host:grpc-port" >&2
  exit 1
fi
export COHERENCE_SERVER_ADDRESS="$1"
ROOT=$(cd "$(dirname "$0")/.." && pwd)
JS_PORT=${TODO_JS_PORT:-7002}
PY_PORT=${TODO_PY_PORT:-7003}
WORK_DIR=$(mktemp -d "${RUNNER_TEMP:-${TMPDIR:-/tmp}}/todo-clients.XXXXXX")
JS_PID=""
PY_PID=""

stop_client() {
  kill "$1" 2>/dev/null || true
  wait "$1" 2>/dev/null || true
}

cleanup() {
  result=$?
  if [ -n "$JS_PID" ]; then stop_client "$JS_PID"; fi
  if [ -n "$PY_PID" ]; then stop_client "$PY_PID"; fi
  if [ "$result" -ne 0 ]; then
    for log in "$WORK_DIR"/*.log; do
      if [ -f "$log" ]; then cat "$log"; fi
    done
  fi
  rm -rf "$WORK_DIR"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

# Refuse occupied HTTP ports before starting either client.
python3 - "$JS_PORT" "$PY_PORT" <<'PY'
import socket
import sys
from contextlib import ExitStack

with ExitStack() as stack:
    for port in sys.argv[1:]:
        listener = stack.enter_context(socket.socket())
        listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        listener.bind(("127.0.0.1", int(port)))
PY

wait_for_http() {
  for _attempt in {1..60}; do
    if ! kill -0 "$1" 2>/dev/null; then
      echo "Client exited before HTTP became ready: $2" >&2
      return 1
    fi
    if curl --fail --silent --connect-timeout 1 --max-time 2 "$2/api/tasks" > /dev/null; then
      return 0
    fi
    sleep 1
  done
  echo "Client HTTP did not become ready: $2" >&2
  return 1
}

echo "Validate JavaScript against $COHERENCE_SERVER_ADDRESS"
(
  cd "$ROOT/js"
  export PORT="$JS_PORT"
  exec node --trace-warnings server.js
) > "$WORK_DIR/js.log" 2>&1 &
JS_PID=$!
wait_for_http "$JS_PID" "http://127.0.0.1:$JS_PORT"
python3 "$ROOT/bin/test-rest-api.py" "http://127.0.0.1:$JS_PORT"
stop_client "$JS_PID"
JS_PID=""

echo "Validate Python against $COHERENCE_SERVER_ADDRESS"
(
  cd "$ROOT/py"
  exec python3 -m quart --app app:app run --host 127.0.0.1 --port "$PY_PORT" --no-reload
) > "$WORK_DIR/py.log" 2>&1 &
PY_PID=$!
wait_for_http "$PY_PID" "http://127.0.0.1:$PY_PORT"
python3 "$ROOT/bin/test-rest-api.py" "http://127.0.0.1:$PY_PORT"
stop_client "$PY_PID"
PY_PID=""

echo "Validate Go against $COHERENCE_SERVER_ADDRESS"
cd "$ROOT/go"
go test -v -count=1 -timeout=120s .
