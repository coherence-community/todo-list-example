/*
 * Copyright (c) 2020, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.cdi.events.MapName;

import com.tangosol.net.NamedMap;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.MapEvent;
import com.tangosol.util.Processors;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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
 * REST API for To Do list management.
 */
@Path("/api/tasks")
@ApplicationScoped
public class ToDoResource
    {
    @Inject
    private NamedMap<String, Task> tasks;

    @Context
    private Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    void createBroadcaster()
        {
        this.broadcaster = sse.newBroadcaster();
        }

    void broadcastEvents(@Observes @MapName("tasks") MapEvent<String, Task> event)
        {
        switch (event.getId())
            {
            case MapEvent.ENTRY_INSERTED:
                broadcaster.broadcast(createEvent("insert", event.getNewValue()));
                break;
            case MapEvent.ENTRY_UPDATED:
                broadcaster.broadcast(createEvent("update", event.getNewValue()));
                break;
            case MapEvent.ENTRY_DELETED:
                broadcaster.broadcast(createEvent("delete", event.getOldValue()));
                break;
            }
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

    @GET
    @Produces(APPLICATION_JSON)
    public Collection<Task> getTasks(@QueryParam("completed") Boolean completed)
        {
        Filter<Task> filter = completed == null
                              ? Filters.always()
                              : Filters.equal(Task::isCompleted, completed);

        return tasks.values(filter, Comparator.comparingLong(Task::getCreatedAt));
        }

    @POST
    @Consumes(APPLICATION_JSON)
    public void createTask(Task task)
        {
        task = new Task(task.getDescription());
        tasks.put(task.getId(), task);
        }

    @DELETE
    public void deleteCompletedTasks()
        {
        tasks.invokeAll(Filters.equal(Task::isCompleted, true),
                        Processors.remove(Filters.always()));
        }

    @DELETE
    @Path("{id}")
    public void deleteTask(@PathParam("id") String id)
        {
        tasks.remove(id);
        }

    @PUT
    @Path("{id}")
    @Consumes(APPLICATION_JSON)
    public Task updateTask(@PathParam("id") String id, Task task)
        {
        String description = task.getDescription();
        Boolean completed = task.isCompleted();

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
    }
