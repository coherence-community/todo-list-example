/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.tangosol.net.Member;
import com.tangosol.net.NamedMap;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;
import com.tangosol.util.Processors;

import edu.umd.cs.findbugs.annotations.Nullable;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.sse.Event;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.PostConstruct;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.reactivestreams.Publisher;

import static io.micronaut.http.MediaType.APPLICATION_JSON;


/**
 * REST API for To Do list management.
 */
@Controller("/api/tasks")
@Singleton
public class ToDoController
    {
    @Inject
    private NamedMap<String, Task> tasks;

    private Flowable<Event<Task>> flowable;

    @PostConstruct
    void createBroadcaster()
        {
        Observable<Event<Task>> source = Observable.<Event<Task>>create(emitter ->
            {
            MapListener<String, Task> listener = new MapListener<>()
                {
                public void entryInserted(MapEvent<String, Task> mapEvent)
                    {
                    emitter.onNext(createEvent("insert", mapEvent.getNewValue()));
                    }

                public void entryUpdated(MapEvent<String, Task> mapEvent)
                    {
                    emitter.onNext(createEvent("update", mapEvent.getNewValue()));
                    }

                public void entryDeleted(MapEvent<String, Task> mapEvent)
                    {
                    emitter.onNext(createEvent("delete", mapEvent.getOldValue()));
                    }
                };
            tasks.addMapListener(listener);
            emitter.setCancellable(() -> tasks.removeMapListener(listener));
            }).share();
        this.flowable = source.toFlowable(BackpressureStrategy.DROP);
        }

    private Event<Task> createEvent(String name, Task task)
        {
        Event<Task> event = Event.of(task);
        event.name(name);
        return event;
        }

    @Get("/events")
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Publisher<Event<? extends Object>> registerEventListener()
        {
        Member member = tasks.getService().getCluster().getLocalMember();
        Event<String> initialEvent = Event.of(member.toString());
        initialEvent.name("begin");

        return Flowable.concatArray(Flowable.fromArray(initialEvent), flowable);
        }

    @Get
    @Produces(APPLICATION_JSON)
    public Collection<Task> getTasks(@Nullable @QueryValue(value = "completed") Boolean completed)
        {
        Filter<Task> filter = completed == null
                              ? Filters.always()
                              : Filters.equal(Task::getCompleted, completed);

        return tasks.values(filter, Comparator.comparingLong(Task::getCreatedAt));
        }

    @Post
    @Consumes(APPLICATION_JSON)
    public void createTask(Task task)
        {
        task = new Task(task.getDescription());
        tasks.put(task.getId(), task);
        }

    @Delete("{id}")
    public void deleteTask(@PathVariable("id") String id)
        {
        tasks.remove(id);
        }

    @Delete
    public void deleteCompletedTasks()
        {
        tasks.invokeAll(Filters.equal(Task::getCompleted, true),
                        Processors.remove(Filters.always()));
        }

    @Put("{id}")
    @Consumes(APPLICATION_JSON)
    public Task updateTask(@PathVariable("id") String id, Task task)
        {
        String description = task.getDescription();
        Boolean completed = task.getCompleted();

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
