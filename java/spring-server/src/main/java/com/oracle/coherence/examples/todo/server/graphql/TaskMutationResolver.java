/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.graphql;

import java.util.Collection;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.service.TaskService;
import graphql.kickstart.tools.GraphQLMutationResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Gunnar Hillert
 */
@Service
public class TaskMutationResolver implements GraphQLMutationResolver
    {

    @Autowired
    private TaskService taskService;

    /**
     * Create a task with the given description.
     * @return the newly created task
     */
    public Task createTask(String description)
        {
        final Task task = new Task(description);
        this.taskService.save(task);
        return task;
        }

    /**
     * Remove all completed tasks and return the tasks left.
     * @return the remaining uncompleted tasks
     */
    public Collection<Task> deleteCompletedTasks()
        {
        return this.taskService.deleteCompletedTasksAndReturnRemainingTasks();
        }

    /**
     * Delete a task and return the deleted task details.
     * @return the deleted task
     */
    public Task deleteTask(String id)
        {
        final Task task = this.taskService.find(id);
        this.taskService.removeById(id);
        return task;
        }

    /**
     * Update a task.
     * @return the updated task
     */
    public Task updateTask(boolean completed, String description, String id)
        {
        final Task task = this.taskService.find(id);
        task.setDescription(description);
        return this.taskService.update(id, task);
        }
    }
