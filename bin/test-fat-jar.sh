#!/usr/bin/env bash

# Copyright (c) 2024, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

set -ex

declare JAR=""
declare JAR_PID=""
declare CURL_PID=""

function validate() {
  java -jar "${JAR}" &
  JAR_PID=$!

  # give some time for the server to come up ...
  sleep 15

  # make a curl request to the SSE endpoint and capture the logs
  curl -N http://localhost:7001/api/tasks/events > log.txt 2>&1 &
  CURL_PID=$!

  # make a curl request to create a task
  curl -d '{"description": "Task1"}' -H 'Content-Type: application/json' -X POST http://localhost:7001/api/tasks

  # get the current tasks
  curl http://localhost:7001/api/tasks
  declare task_id=$(curl http://localhost:7001/api/tasks | grep -o '"id":"[^"]*' | grep -o '[^"]*$')

  # mark the task as completed
  curl -d '{"completed": "true"}' -H 'Content-Type: application/json' -X PUT  http://localhost:7001/api/tasks/"${task_id}"

  # delete the task
  curl -X DELETE  http://localhost:7001/api/tasks/"${task_id}"

  cat log.txt

  kill -3 $CURL_PID
  CURL_PID=""

  grep "event: insert" log.txt
  grep "event: update" log.txt
  grep "event: delete" log.txt
}

function cleanup() {
  if [ -n "${JAR_PID}" ]; then
    kill -9 ${JAR_PID}
  fi

  if [ -n "${CURL_PID}" ]; then
      kill -9 ${CURL_PID}
    fi
}

trap cleanup EXIT

while getopts "d:" OPTION; do
  case "${OPTION}" in
  d)
    JAR=${OPTARG}
    validate
    ;;
  ?)
    echo "Usage: $(basename "$0") -d [fat-jar-to-test]"
    exit 1
    ;;
  esac
done