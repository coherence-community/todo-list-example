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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * @author Gunnar Hillert
 */
@Controller
public class TaskMutationController
    {

    @Autowired
    private TaskService taskService;

    /**
     * Create a task with the given description.
     * @return the newly created task
     */
    @MutationMapping
    public Task createTask(@Argument String description)
        {
        final Task task = new Task(description);
        this.taskService.save(task);
        return task;
        }

    /**
     * Remove all completed tasks and return the tasks left.
     * @return the remaining uncompleted tasks
     */
    @MutationMapping
    public Collection<Task> deleteCompletedTasks()
        {
        return this.taskService.deleteCompletedTasksAndReturnRemainingTasks();
        }

    /**
     * Delete a task and return the deleted task details.
     * @return the deleted task
     */
    @MutationMapping
    public Task deleteTask(@Argument String id)
        {
        final Task task = this.taskService.find(id);
        this.taskService.removeById(id);
        return task;
        }

    /**
     * Update a task.
     * @return the updated task
     */
    @MutationMapping
    public Task updateTask(@Argument boolean completed, @Argument String description, @Argument String id)
        {
        final Task task = this.taskService.find(id);
        task.setCompleted(completed);
        task.setDescription(description);
        return this.taskService.update(id, task);
        }
    }
