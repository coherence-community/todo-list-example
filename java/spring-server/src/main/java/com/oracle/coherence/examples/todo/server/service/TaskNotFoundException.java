/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.service;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * An exception indicating that a {@link com.oracle.coherence.examples.todo.server.model.Task} was not found.
 * @author Gunnar Hillert
 */
public class TaskNotFoundException
        extends RuntimeException implements GraphQLError
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

        @Override
        public List<SourceLocation> getLocations()
            {
            return null;
            }

        @Override
        public ErrorClassification getErrorType()
            {
            return null;
            }
    }
