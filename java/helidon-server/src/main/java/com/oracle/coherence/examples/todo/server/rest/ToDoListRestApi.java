/*
 * Copyright (c) 2020, 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.rest;

import com.oracle.coherence.examples.todo.server.Task;
import com.oracle.coherence.examples.todo.server.ToDoListService;

import java.util.Collection;

import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;
import javax.json.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * REST facade for To Do list API.
 */
@Path("/api/tasks")
@ApplicationScoped
public class ToDoListRestApi
    {
    @Inject
    private ToDoListService api;

    @Context
    private Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    void createBroadcaster()
        {
        this.broadcaster = sse.newBroadcaster();
        }

    private OutboundSseEvent createEvent(String name, Task task)
        {
        return sse.newEventBuilder()
                    .name(name)
                    .data(Task.class, task)
                    .mediaType(APPLICATION_JSON_TYPE)
                    .build();
        }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void registerEventListener(@Context SseEventSink eventSink)
        {
        broadcaster.register(eventSink);
        }

    @POST
    @Consumes(APPLICATION_JSON)
    public Task createTask(JsonObject task)
        {
        return api.createTask(task.getString("description"));
        }

    @GET
    @Produces(APPLICATION_JSON)
    public Collection<Task> getTasks(@QueryParam("completed") Boolean completed)
        {
        return api.getTasks(completed);
        }

    @DELETE
    @Path("{id}")
    public Task deleteTask(@PathParam("id") String id)
        {
        return api.deleteTask(id);
        }

    @DELETE
    public boolean deleteCompletedTasks()
        {
        return api.deleteCompletedTasks();
        }

    @PUT
    @Path("{id}")
    @Consumes(APPLICATION_JSON)
    public Task updateTask(@PathParam("id") String id, JsonObject task)
        {
        if (task.containsKey("description"))
            {
            return api.updateDescription(id, task.getString("description"));
            }
        else if (task.containsKey("completed"))
            {
            return api.updateCompletionStatus(id, task.getBoolean("completed"));
            }

        throw new IllegalArgumentException("either description or completion status must be specified");
        }
    }
