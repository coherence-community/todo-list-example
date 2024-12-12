/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.tangosol.coherence.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry-point to the To Do Application.
 * @author Gunnar Hillert
 */
@SpringBootApplication
public class TodoListSpringServerApplication
    {
    public static void main(String[] args)
        {
        SpringApplication.run(TodoListSpringServerApplication.class, args);
        System.out.println("coherence.tracing.ratio=" + Config.getProperty("coherence.tracing.ratio", "undefined"));
        }
    }
