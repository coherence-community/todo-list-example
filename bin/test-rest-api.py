#!/usr/bin/env python3
# Copyright (c) 2026, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

"""Exercise CRUD, filters, bulk removal and SSE on a disposable empty server."""

import json
import sys
import threading
import time
import urllib.request
import uuid


SSE_TIMEOUT = 10


def validate(base_url, graphql=False):
    base_url = base_url.rstrip("/")
    prefix = "qualification-" + uuid.uuid4().hex
    events = {}
    condition = threading.Condition()
    reader_errors = []
    owned_ids = set()

    def request(method, path="", body=None):
        data = None if body is None else json.dumps(body).encode()
        req = urllib.request.Request(base_url + "/api/tasks" + path, data=data,
                                     method=method,
                                     headers={"Content-Type": "application/json"})
        with urllib.request.urlopen(req, timeout=15) as response:
            content = response.read()
            return json.loads(content) if content and method == "GET" else None

    def read_events():
        try:
            with urllib.request.urlopen(base_url + "/api/tasks/events", timeout=30) as response:
                event = ""
                data = []
                for raw_line in response:
                    line = raw_line.decode().strip()
                    if line.startswith("event:"):
                        event = line[6:].strip()
                    elif line.startswith("data:"):
                        data.append(line[5:].lstrip())
                    elif not line:
                        payload = "\n".join(data)
                        if event in ("insert", "update", "delete") and prefix in payload:
                            task = json.loads(payload)
                            with condition:
                                events.setdefault(task["id"], set()).add(event)
                                condition.notify_all()
                        event, data = "", []
            raise OSError("SSE stream closed")
        except (OSError, ValueError, KeyError) as error:
            with condition:
                reader_errors.append(error)
                condition.notify_all()

    def wait_for_event(name, task_id, timeout):
        with condition:
            condition.wait_for(lambda: name in events.get(task_id, set()) or reader_errors,
                               timeout=timeout)
            assert not reader_errors, "SSE reader failed: " + str(reader_errors[0])
            return name in events.get(task_id, set())

    assert request("GET") == [], "Use a disposable empty server for bulk-removal tests"
    try:
        # A response header or 'begin' event need not mean the map listener is ready.
        # Observe a real update on a separate task before testing the first insert.
        request("POST", body={"description": prefix + "-sse-probe"})
        probe = request("GET")[0]
        owned_ids.add(probe["id"])
        threading.Thread(target=read_events, daemon=True).start()
        deadline = time.monotonic() + SSE_TIMEOUT
        attempt = 0
        while True:
            remaining = deadline - time.monotonic()
            assert remaining > 0, "SSE subscription did not receive the readiness probe"
            attempt += 1
            request("PUT", "/" + probe["id"],
                    {"description": prefix + "-sse-probe-" + str(attempt)})
            if wait_for_event("update", probe["id"], min(0.1, remaining)):
                break
        request("DELETE", "/" + probe["id"])
        assert request("GET") == []

        request("POST", body={"description": prefix})
        tasks = request("GET")
        assert len(tasks) == 1, tasks
        task = tasks[0]
        owned_ids.add(task["id"])
        assert task["description"] == prefix and task["completed"] is False, task
        assert isinstance(task["createdAt"], (int, float)), task
        assert wait_for_event("insert", task["id"], SSE_TIMEOUT), (
            "Missing SSE insert for " + task["id"])
        assert request("GET", "?completed=true") == []

        request("PUT", "/" + task["id"], {"description": prefix + "-updated"})
        request("PUT", "/" + task["id"], {"completed": True})
        completed = request("GET", "?completed=true")
        assert len(completed) == 1 and completed[0]["id"] == task["id"], completed
        assert completed[0]["description"] == prefix + "-updated", completed
        if graphql:
            query = "{ tasks(completed: true) { id description completed } }"
            req = urllib.request.Request(base_url + "/graphql",
                    data=json.dumps({"query": query}).encode(),
                    headers={"Content-Type": "application/json"})
            with urllib.request.urlopen(req, timeout=15) as response:
                result = json.load(response)
            assert not result.get("errors"), result
            assert any(item["id"] == task["id"] and item["completed"] is True
                       for item in result["data"]["tasks"]), result

        request("DELETE", "/" + task["id"])
        assert request("GET") == []
        for name in ("update", "delete"):
            assert wait_for_event(name, task["id"], SSE_TIMEOUT), (
                "Missing SSE " + name + " for " + task["id"])

        request("POST", body={"description": prefix + "-bulk"})
        task = request("GET")[0]
        owned_ids.add(task["id"])
        request("PUT", "/" + task["id"], {"completed": True})
        request("DELETE")
        assert request("GET") == []

        print("PASS: CRUD, completed filter, bulk removal, insert/update/delete SSE"
              + (", GraphQL query" if graphql else ""))
    finally:
        for task in request("GET"):
            if task["id"] in owned_ids or task["description"].startswith(prefix):
                request("DELETE", "/" + task["id"])


if __name__ == "__main__":
    validate(sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:7001",
             graphql="--graphql" in sys.argv[2:])
