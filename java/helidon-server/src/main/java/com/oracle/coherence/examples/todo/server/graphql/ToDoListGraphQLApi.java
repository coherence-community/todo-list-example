/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.graphql;

import com.oracle.coherence.examples.todo.server.Task;
import com.oracle.coherence.examples.todo.server.ToDoListService;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL facade for To Do list API.
 */
@GraphQLApi
@ApplicationScoped
public class ToDoListGraphQLApi
    {
    @Inject
    private ToDoListService api;
    
    //----- API methods -----------------------------------------------------

    @Mutation
    @Description("Create a task with the given description")
    public Task createTask(@Name("description") String description)
        {
        return api.createTask(description);
        }

    @Query
    @Description("Query tasks and optionally specify only completed")
    public Collection<Task> getTasks(@Name("completed") Boolean completed)
        {
        return api.getTasks(completed);
        }

    @Query
    @Description("Find a given task using the task id")
    public Task findTask(@Name("id") String id) throws NotFoundException
        {
        return api.findTask(id);
        }

    @Mutation
    @Description("Delete a task and return the deleted task details")
    public Task deleteTask(@Name("id") String id) throws NotFoundException
        {
        return api.deleteTask(id);
        }

    @Mutation
    @Description("Remove all completed tasks and return whether any tasks have been removed")
    public boolean deleteCompletedTasks()
        {
        return api.deleteCompletedTasks();
        }

    @Mutation
    @Description("Update task description")
    public Task updateDescription(@Name("id") String id,
                                  @Name("description") String description) throws NotFoundException
        {
        return api.updateDescription(id, description);
        }

    @Mutation
    @Description("Update task completion status")
    public Task updateCompletionStatus(@Name("id") String id,
                                       @Name("completed") boolean completed) throws NotFoundException
        {
        return api.updateCompletionStatus(id, completed);
        }
    }
