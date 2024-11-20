/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.rest;

import com.oracle.coherence.examples.todo.server.Task;
import com.oracle.coherence.examples.todo.server.TaskRepository;
import com.oracle.coherence.examples.todo.server.ToDoListService;
import com.tangosol.net.Cluster;
import com.tangosol.net.Member;

import java.util.Collection;

import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * REST facade for To Do list API.
 */
@Path("/api/tasks")
@ApplicationScoped
public class ToDoListRestApi
    {
    @Inject
    private ToDoListService api;

    @Inject
    private TaskRepository tasks;

    @Inject
    private Cluster cluster;

    @Context
    private Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    void createBroadcaster()
        {
        this.broadcaster = sse.newBroadcaster();

        tasks.addListener(
                tasks.listener()
                     .onInsert(task -> broadcaster.broadcast(createEvent("insert", task)))
                     .onUpdate(task -> broadcaster.broadcast(createEvent("update", task)))
                     .onRemove(task -> broadcaster.broadcast(createEvent("delete", task)))
                     .build());
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

        Member member = cluster.getLocalMember();
        eventSink.send(sse.newEvent("begin", member.toString()));
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
