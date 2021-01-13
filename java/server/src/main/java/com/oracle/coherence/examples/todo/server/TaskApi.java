/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.tangosol.net.NamedMap;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.Processors;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;


/**
 * API to expose GraphQL endpoints.
 *
 * @author Tim Middleton
 */
@GraphQLApi
@ApplicationScoped
public class TaskApi
    {
    //----- TaskApi methods -------------------------------------------------

    /**
     * Create a {@link Task}.
     *
     * @param description task description
     *
     * @return the created {@link Task}
     */
    @Mutation
    @Description("Create a task with the given description")
    public Task createTask(@Name("description") String description)
        {
        Objects.requireNonNull(description, "Description must be provided");
        Task task = new Task(description);
        tasks.put(task.getId(), task);
        return task;
        }

    /**
     * Query {@link Task}s.
     *
     * @param completed optionally specify completion status
     *
     * @return a {@link Collection} of {@link Task}s
     */
    @Query
    @Description("Query tasks and optionally specify only completed")
    public Collection<Task> getTasks(@Name("completed") Boolean completed)
        {
        Filter<Task> filter = completed == null
                              ? Filters.always()
                              : Filters.equal(Task::getCompleted, completed);

        return tasks.values(filter, Comparator.comparingLong(Task::getCreatedAt));
        }

    /**
     * Find a {@link Task}.
     *
     * @param id task id
     *
     * @return the {@link Task} with the given id
     *
     * @throws TaskNotFoundException if the task was not found
     */
    @Query
    @Description("Find a given task using the task id")
    public Task findTask(@Name("id") String id) throws TaskNotFoundException
        {
        return Optional.ofNullable(tasks.get(id))
                .orElseThrow(() -> new TaskNotFoundException(MESSAGE + id));
        }

    /**
     * Delete a {@link Task}.
     *
     * @param id task to delete
     *
     * @return the deleted {@link Task}
     *
     * @throws TaskNotFoundException if the task was not found
     */
    @Mutation
    @Description("Delete a task and return the deleted task details")
    public Task deleteTask(@Name("id") String id) throws TaskNotFoundException
        {
        return Optional.ofNullable(tasks.remove(id))
                .orElseThrow(() -> new TaskNotFoundException(MESSAGE + id));
        }

    /**
     * Remove all completed {@link Task}s.
     *
     * @return the number of {@link Task}s removed
     */
    @Mutation
    @Description("Remove all completed tasks and return the tasks left")
    public Collection<Task> deleteCompletedTasks()
        {
        tasks.invokeAll(Filters.equal(Task::getCompleted, true), Processors.remove(Filters.always()));
        return tasks.values();
        }

    /**
     * Update a {@link Task}.
     *
     * @param id          task to update
     * @param description optional description
     * @param completed   optional completed
     *
     * @return the updated {@link Task}
     *
     * @throws TaskNotFoundException if the task was not found
     */
    @Mutation
    @Description("Update a task")
    public Task updateTask(@Name("id") String id,
                           @Name("description") String description,
                           @Name("completed") Boolean completed)
            throws TaskNotFoundException
        {
        try
            {
            return tasks.compute(id, (k, v) ->
                {
                Objects.requireNonNull(v);

                if (description != null)
                    {
                    v.setDescription(description);
                    }
                if (completed != null)
                    {
                    v.setCompleted(completed);
                    }
                return v;
                });
            }
        catch (Exception e)
            {
            throw new TaskNotFoundException(MESSAGE + id);
            }
        }

    //----- constants -------------------------------------------------------

    private static final String MESSAGE = "Unable to find task with id ";

    //----- data members ----------------------------------------------------

    @Inject
    private NamedMap<String, Task> tasks;
    }
