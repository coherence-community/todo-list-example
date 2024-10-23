/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.examples.todo.test.common.BaseTodoListTests;

import io.helidon.microprofile.server.Server;

import io.opentelemetry.api.GlobalOpenTelemetry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class TodoListIT
        extends BaseTodoListTests
    {
    @BeforeAll
    static void startServer()
        {
        GlobalOpenTelemetry.resetForTest();
        System.setProperty("coherence.localhost", "127.0.0.1");
        System.setProperty("coherence.ttl", "0");
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("coherence.wka", "127.0.0.1");
        SERVER = Server.builder().port(0).build().start();
        }

    @AfterAll
    static void stopServer()
        {
        if (SERVER != null)
            {
            SERVER.stop();
            try
                {
                Thread.sleep(5000);
                }
            catch (InterruptedException e)
                {
                throw new RuntimeException(e);
                }
            GlobalOpenTelemetry.resetForTest();
            }
        }

    // ----- helper methods -------------------------------------------------

    protected String getUrl()
        {
        return "http://localhost:" + SERVER.port();
        }

    // ----- data members ---------------------------------------------------

    protected static Server SERVER;
    }
