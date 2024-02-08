/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.examples.todo.test.common.BaseTodoListTests;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoListTests
        extends BaseTodoListTests
    {

    // ----- helper methods -------------------------------------------------

    @Override
    protected String getUrl()
        {
        return "http://localhost:" + port;
        }

    // ----- data members ---------------------------------------------------

    @LocalServerPort
    protected int port;
    }
