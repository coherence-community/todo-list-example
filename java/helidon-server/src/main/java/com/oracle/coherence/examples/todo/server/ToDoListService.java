/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.tangosol.util.Filter;

import java.util.Collection;
import java.util.Objects;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.NotFoundException;

import static com.tangosol.util.Filters.always;
import static com.tangosol.util.Filters.equal;
import static com.tangosol.util.Filters.isTrue;

/**
 * To Do List API implementation that can be used by all API facades.
 *
 * @author Aleks Seovic
 */
@ApplicationScoped
public class ToDoListService
    {
    //----- constants -------------------------------------------------------

    private static final String MESSAGE = "Unable to find task with id ";

    //----- data members ----------------------------------------------------

    @Inject
    protected TaskRepository tasks;

    //----- ToDoListApi methods ---------------------------------------------

    /**
     * Create a {@link Task}.
     *
     * @param description task description
     *
     * @return the created {@link Task}
     */
    public Task createTask(String description)
        {
        Objects.requireNonNull(description, "description is required");
        return tasks.save(new Task(description));
        }

    /**
     * Query {@link Task}s.
     *
     * @param completed optionally specify completion status
     *
     * @return a {@link Collection} of {@link Task}s
     */
    public Collection<Task> getTasks(Boolean completed)
        {
        Filter<Task> filter = completed == null
                              ? always()
                              : equal(Task::getCompleted, completed);

        return tasks.getAllOrderedBy(filter, Task::getCreatedAt);
        }

    /**
     * Find a {@link Task}.
     *
     * @param id task id
     *
     * @return the {@link Task} with the given id
     *
     * @throws NotFoundException if the task was not found
     */
    public Task findTask(String id)
        {
        return Optional
                .ofNullable(tasks.get(id))
                .orElseThrow(() -> new NotFoundException(MESSAGE + id));
        }

    /**
     * Delete a {@link Task}.
     *
     * @param id task to delete
     *
     * @return the deleted {@link Task}
     *
     * @throws NotFoundException if the task was not found
     */
    public Task deleteTask(String id)
        {
        return Optional
                .ofNullable(tasks.removeById(id, true))
                .orElseThrow(() -> new NotFoundException(MESSAGE + id));
        }

    /**
     * Remove all completed {@link Task}s.
     *
     * @return whether any tasks have been removed
     */
    public boolean deleteCompletedTasks()
        {
        return tasks.removeAll(isTrue(Task::getCompleted));
        }

    /**
     * Update a {@link Task} description.
     *
     * @param id           task to update
     * @param description  new description
     *
     * @return the updated {@link Task}
     *
     * @throws NotFoundException if the task was not found
     */
    public Task updateDescription(String id, String description)
        {
        return Optional
                .ofNullable(tasks.update(id, Task::setDescription, description))
                .orElseThrow(() -> new NotFoundException(MESSAGE + id));
        }

    /**
     * Update a {@link Task} completion status.
     *
     * @param id         task to update
     * @param completed  new completion status
     *
     * @return the updated {@link Task}
     *
     * @throws NotFoundException if the task was not found
     */
    public Task updateCompletionStatus(String id, boolean completed)
        {
        return Optional
                .ofNullable(tasks.update(id, Task::setCompleted, completed))
                .orElseThrow(() -> new NotFoundException(MESSAGE + id));
        }
    }
