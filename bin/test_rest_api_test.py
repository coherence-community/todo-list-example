#!/usr/bin/env python3
# Copyright (c) 2026, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.

"""Regression tests for the REST/SSE validator using a disposable HTTP server."""

import contextlib
import importlib.util
import io
import json
from pathlib import Path
import queue
import threading
import time
import unittest
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlsplit
from unittest.mock import patch


spec = importlib.util.spec_from_file_location("rest_api", Path(__file__).with_name("test-rest-api.py"))
rest_api = importlib.util.module_from_spec(spec)
spec.loader.exec_module(rest_api)


class TaskServer(ThreadingHTTPServer):
    def __init__(self, *, delay=0, begin=True, drop=None, drop_first_insert=False, stream_error=None):
        super().__init__(("127.0.0.1", 0), TaskHandler)
        self.delay = delay
        self.begin = begin
        self.drop = drop
        self.drop_first_insert = drop_first_insert
        self.stream_error = stream_error
        self.tasks = {}
        self.next_id = 0
        self.subscribers = []
        self.lock = threading.Lock()
        self.stopping = threading.Event()
        self.early_updates = 0

    def broadcast(self, name, task):
        probe = "-sse-probe" in task["description"]
        if not self.subscribers and probe and name == "update":
            self.early_updates += 1
        if not probe and name == "insert" and self.drop_first_insert:
            self.drop_first_insert = False
            return
        if not probe and name == self.drop:
            return
        for subscriber in self.subscribers:
            subscriber.put((name, dict(task)))


class TaskHandler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, *_args):
        pass

    def respond(self, value, status=200):
        data = json.dumps(value).encode()
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def body(self):
        return json.loads(self.rfile.read(int(self.headers["Content-Length"])))

    def do_GET(self):
        if urlsplit(self.path).path.endswith("/events"):
            self.stream()
            return
        with self.server.lock:
            tasks = list(self.server.tasks.values())
            if parse_qs(urlsplit(self.path).query).get("completed") == ["true"]:
                tasks = [task for task in tasks if task["completed"]]
            self.respond(tasks)

    def stream(self):
        if self.server.stream_error == "http":
            self.respond({"error": "SSE unavailable"}, 503)
            return
        self.send_response(200)
        self.send_header("Content-Type", "text/event-stream")
        self.send_header("Connection", "close")
        self.end_headers()
        self.close_connection = True
        if self.server.stream_error == "eof":
            return
        subscriber = queue.Queue()
        try:
            if self.server.begin:
                # Deliberately advertise 'begin' before the task listener exists.
                self.wfile.write(b"event: begin\ndata: member\n\n")
                self.wfile.flush()
            if self.server.stopping.wait(self.server.delay):
                return
            with self.server.lock:
                self.server.subscribers.append(subscriber)
            while not self.server.stopping.is_set():
                try:
                    name, task = subscriber.get(timeout=0.05)
                except queue.Empty:
                    continue
                self.wfile.write(f"event: {name}\ndata: {json.dumps(task)}\n\n".encode())
                self.wfile.flush()
        except (BrokenPipeError, ConnectionResetError):
            pass
        finally:
            with self.server.lock:
                if subscriber in self.server.subscribers:
                    self.server.subscribers.remove(subscriber)

    def do_POST(self):
        body = self.body()
        with self.server.lock:
            if self.path == "/graphql":
                self.respond({"data": {"tasks": list(self.server.tasks.values())}})
                return
            self.server.next_id += 1
            task = {"id": str(self.server.next_id), "description": body["description"],
                    "completed": False, "createdAt": int(time.time() * 1000)}
            self.server.tasks[task["id"]] = task
            self.server.broadcast("insert", task)
            self.respond(task)

    def do_PUT(self):
        body = self.body()
        with self.server.lock:
            task = self.server.tasks[self.path.rsplit("/", 1)[1]]
            task.update(body)
            self.server.broadcast("update", task)
            self.respond(task)

    def do_DELETE(self):
        with self.server.lock:
            if self.path == "/api/tasks":
                ids = [task["id"] for task in self.server.tasks.values() if task["completed"]]
            else:
                ids = [self.path.rsplit("/", 1)[1]]
            for task_id in ids:
                task = self.server.tasks.pop(task_id)
                self.server.broadcast("delete", task)
            self.respond(None)


class RestApiTest(unittest.TestCase):
    def validate(self, *, timeout=5, graphql=False, **options):
        server = TaskServer(**options)
        worker = threading.Thread(target=server.serve_forever, daemon=True)
        worker.start()
        try:
            with patch.object(rest_api, "SSE_TIMEOUT", timeout), contextlib.redirect_stdout(io.StringIO()):
                rest_api.validate(f"http://127.0.0.1:{server.server_port}", graphql=graphql)
        finally:
            server.stopping.set()
            server.shutdown()
            server.server_close()
            worker.join(timeout=2)
            self.assertEqual({}, server.tasks, "Validator must clean up its probe and test tasks")
        return server

    def test_begin_before_delayed_listener(self):
        server = self.validate(delay=1.25, graphql=True)
        self.assertGreater(server.early_updates, 0)

    def test_stream_without_begin(self):
        self.validate(begin=False, delay=0.05)

    def test_listener_never_ready(self):
        with self.assertRaisesRegex(AssertionError, "readiness probe"):
            self.validate(delay=5, timeout=0.3)

    def test_probe_cannot_mask_missing_task_events(self):
        for name in ("insert", "update", "delete"):
            with self.subTest(event=name), self.assertRaisesRegex(AssertionError, "Missing SSE " + name):
                self.validate(drop=name, timeout=0.3)

    def test_bulk_insert_cannot_mask_missing_first_insert(self):
        with self.assertRaisesRegex(AssertionError, "Missing SSE insert"):
            self.validate(drop_first_insert=True, timeout=0.3)

    def test_stream_http_error(self):
        with self.assertRaisesRegex(AssertionError, "SSE reader failed: HTTP Error 503"):
            self.validate(stream_error="http")

    def test_stream_closes_before_readiness(self):
        with self.assertRaisesRegex(AssertionError, "SSE reader failed: SSE stream closed"):
            self.validate(stream_error="eof")


if __name__ == "__main__":
    unittest.main()
