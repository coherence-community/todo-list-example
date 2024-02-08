/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.examples.todo.test.common.BaseTodoListTests;

import io.micronaut.context.ApplicationContext;

import io.micronaut.runtime.server.EmbeddedServer;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

@MicronautTest(application=Application.class)
public class TodoListIT
        extends BaseTodoListTests
    {
    // ----- helper methods -------------------------------------------------

    @Override
    protected String getUrl()
        {
        return server.getURL().toString();
        }

    // ----- data members ---------------------------------------------------

    /**
     * The server under test.
     */
    @Inject
    EmbeddedServer server;

    /**
     * The application context.
     */
    @Inject
    ApplicationContext ctx;
    }
