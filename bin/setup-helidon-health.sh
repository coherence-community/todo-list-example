#!/usr/bin/env bash

# Copyright (c) 2026, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

set -euo pipefail
umask 077

if [ "$#" -ne 2 ] || [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 KUBECTL_CONTEXT NAMESPACE" >&2
  exit 1
fi
CONTEXT=$1
NAMESPACE=$2
KUBECTL=(kubectl --context "$CONTEXT" --namespace "$NAMESPACE")
SECRETS=(todo-health-auth-v1 todo-health-stores-v1 todo-health-ca-v1)

for tool in kubectl keytool openssl; do
  command -v "$tool" > /dev/null || { echo "Required tool missing: $tool" >&2; exit 1; }
done
"${KUBECTL[@]}" get namespace "$NAMESPACE" > /dev/null
EXISTING=$("${KUBECTL[@]}" get secret "${SECRETS[@]}" --ignore-not-found -o name)
if [ -n "$EXISTING" ]; then
  echo "Health Secrets already exist in $CONTEXT/$NAMESPACE; refusing to replace them." >&2
  exit 1
fi

MATERIAL=$(mktemp -d "${TMPDIR:-/tmp}/todo-health.XXXXXXXX")
CREATED=()
# shellcheck disable=SC2329
cleanup() {
  local status=$?
  rm -rf "$MATERIAL"
  if [ "$status" -ne 0 ] && [ "${#CREATED[@]}" -gt 0 ]; then
    "${KUBECTL[@]}" delete secret "${CREATED[@]}" --ignore-not-found > /dev/null || true
  fi
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

printf '%s' 'health-operator' > "$MATERIAL/username"
openssl rand -hex 32 | tr -d '\n' > "$MATERIAL/password"
openssl rand -hex 32 | tr -d '\n' > "$MATERIAL/store-password"
keytool -genkeypair -alias health -keyalg RSA -keysize 2048 -validity 365 \
  -dname CN=todo-health.local -ext SAN=dns:todo-health.local \
  -keystore "$MATERIAL/server.jks" -storetype JKS \
  -storepass:file "$MATERIAL/store-password" -keypass:file "$MATERIAL/store-password" \
  > /dev/null
keytool -exportcert -rfc -alias health -keystore "$MATERIAL/server.jks" \
  -storepass:file "$MATERIAL/store-password" -file "$MATERIAL/ca.pem" > /dev/null

"${KUBECTL[@]}" create secret generic "${SECRETS[0]}" \
  --from-file=username="$MATERIAL/username" --from-file=password="$MATERIAL/password"
CREATED+=("${SECRETS[0]}")
"${KUBECTL[@]}" create secret generic "${SECRETS[1]}" \
  --from-file=server.jks="$MATERIAL/server.jks" --from-file=store-password="$MATERIAL/store-password"
CREATED+=("${SECRETS[1]}")
"${KUBECTL[@]}" create secret generic "${SECRETS[2]}" --from-file=ca.pem="$MATERIAL/ca.pem"
CREATED+=("${SECRETS[2]}")
for secret in "${SECRETS[@]}"; do
  "${KUBECTL[@]}" patch secret "$secret" --type=merge -p '{"immutable":true}'
done
echo "Health Secrets ready in $CONTEXT/$NAMESPACE (certificate expires in 365 days)."
