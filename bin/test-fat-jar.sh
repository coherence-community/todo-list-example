#!/usr/bin/env bash

# Copyright (c) 2024, 2026, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

set -euo pipefail

JAR=""
JAR_PID=""
WORK_DIR=$(mktemp -d "${TMPDIR:-/tmp}/todo-fat-jar.XXXXXX")

# Invoked by the EXIT trap below.
# shellcheck disable=SC2329
cleanup() {
  if [ -n "$JAR_PID" ]; then
    kill "$JAR_PID" 2>/dev/null || true
    wait "$JAR_PID" 2>/dev/null || true
  fi
  rm -rf "$WORK_DIR"
}
trap 'cleanup' EXIT

while getopts "d:" OPTION; do
  case "$OPTION" in
    d) JAR=$OPTARG ;;
    *) exit 1 ;;
  esac
done
if [ -z "$JAR" ]; then
  echo "Usage: $0 -d fat-jar-to-test" >&2
  exit 1
fi
if curl --silent --max-time 1 http://127.0.0.1:7001/ > /dev/null; then
  echo "Port 7001 is already serving HTTP; use a dedicated test environment" >&2
  exit 1
fi

# JAVA_TOOL_OPTIONS can select a Spring profile or dedicated cluster port.
# Leave coherence.security.mode unset to test the hardened default.
java -Dotel.sdk.disabled=true -jar "$JAR" > "$WORK_DIR/server.log" 2>&1 &
JAR_PID=$!
for _attempt in {1..90}; do
  if ! kill -0 "$JAR_PID" 2>/dev/null; then
    cat "$WORK_DIR/server.log"
    exit 1
  fi
  if curl --fail --silent http://127.0.0.1:7001/api/tasks > /dev/null; then
    if python3 "$(dirname "$0")/test-rest-api.py" http://127.0.0.1:7001 --graphql; then
      exit 0
    fi
    cat "$WORK_DIR/server.log"
    exit 1
  fi
  sleep 1
done
cat "$WORK_DIR/server.log"
echo "Server did not become ready" >&2
exit 1
