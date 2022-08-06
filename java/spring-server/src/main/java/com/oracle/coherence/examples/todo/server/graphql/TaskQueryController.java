/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.graphql;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;

/**
 * @author Gunnar Hillert
 */
@Controller
public class TaskQueryController
    {

    @Autowired
    private TaskService taskService;

    /**
     * Find a single task by the provided id.
     *
     * @param id the id of the task to find
     * @return the retrieved task
     */
    @QueryMapping
    public Task findTask(@Argument String id)
        {
        return taskService.find(id);
        }

    /**
     * Retrieve all tasks if completed is {@code false}. Otherwise, return only completed tasks.
     * @param completed if true return only completed tasks
     * @return a colection of tasks
     */
    @QueryMapping
    public Collection<Task> tasks(@Argument boolean completed)
        {
        return taskService.findAll(completed);
        }
    }
