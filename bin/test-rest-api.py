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


def validate(base_url, graphql=False):
    base_url = base_url.rstrip("/")
    prefix = "qualification-" + uuid.uuid4().hex
    events = []
    condition = threading.Condition()
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
                for raw_line in response:
                    line = raw_line.decode().strip()
                    if line.startswith("event:"):
                        event = line[6:].strip()
                    elif line.startswith("data:") and prefix in line:
                        with condition:
                            events.append(event)
                            condition.notify_all()
        except (OSError, ValueError):
            pass  # The main thread reports missing events with the observed list.

    assert request("GET") == [], "Use a disposable empty server for bulk-removal tests"
    threading.Thread(target=read_events, daemon=True).start()
    time.sleep(1)  # Allow the SSE subscription to register before the first insert.
    try:
        request("POST", body={"description": prefix})
        tasks = request("GET")
        assert len(tasks) == 1, tasks
        task = tasks[0]
        owned_ids.add(task["id"])
        assert task["description"] == prefix and task["completed"] is False, task
        assert isinstance(task["createdAt"], (int, float)), task
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

        request("POST", body={"description": prefix + "-bulk"})
        task = request("GET")[0]
        owned_ids.add(task["id"])
        request("PUT", "/" + task["id"], {"completed": True})
        request("DELETE")
        assert request("GET") == []

        deadline = time.monotonic() + 10
        with condition:
            while not all(name in events for name in ("insert", "update", "delete")):
                remaining = deadline - time.monotonic()
                assert remaining > 0, "Missing SSE events: " + repr(events)
                condition.wait(remaining)
        print("PASS: CRUD, completed filter, bulk removal, insert/update/delete SSE"
              + (", GraphQL query" if graphql else ""))
    finally:
        for task in request("GET"):
            if task["id"] in owned_ids or task["description"].startswith(prefix):
                request("DELETE", "/" + task["id"])


if __name__ == "__main__":
    validate(sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:7001",
             graphql="--graphql" in sys.argv[2:])
