/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

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
 */
@ApplicationScoped
public class ToDoListService
    {
    //----- constants -------------------------------------------------------

    private static final String MESSAGE = "Unable to find task with id ";

    //----- data members ----------------------------------------------------

    //----- API methods -----------------------------------------------------

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
        return null;
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
        return null;
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
        return null;
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
        return null;
        }

    /**
     * Remove all completed {@link Task}s.
     *
     * @return whether any tasks have been removed
     */
    public boolean deleteCompletedTasks()
        {
        return false;
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
        return null;
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
        return null;
        }
    }
