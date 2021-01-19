/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

/**
 * An exception indicating that a {@link Task} was not found.
 */
public class TaskNotFoundException
        extends Exception
    {
    /**
     * Create the exception.
     *
     * @param message reason for the exception.
     */
    public TaskNotFoundException(String message)
        {
        super(message);
        }
    }
