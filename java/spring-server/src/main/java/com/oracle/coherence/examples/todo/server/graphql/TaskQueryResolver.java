/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.graphql;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.service.TaskService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Gunnar Hillert
 */
@Service
public class TaskQueryResolver implements GraphQLQueryResolver
    {

    @Autowired
    private TaskService taskService;

    /**
     * Find a single task by the provided id.
     *
     * @param id the id of the task to find
     * @return the retrieved task
     */
    public Task findTask(String id)
        {
        return taskService.find(id);
        }

    /**
     * Retrieve all tasks if completed is {@code false}. Otherwise, return only completed tasks.
     * @param completed if true return only completed tasks
     * @return a colection of tasks
     */
    public Collection<Task> tasks(boolean completed)
        {
        return taskService.findAll(completed);
        }
    }
