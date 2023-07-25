/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.rest;

import com.oracle.coherence.examples.todo.server.Task;
import com.oracle.coherence.examples.todo.server.TaskRepository;
import com.tangosol.net.Cluster;
import com.tangosol.net.Member;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import io.micronaut.core.annotation.Nullable;

import io.micronaut.http.MediaType;

import io.micronaut.http.annotation.*;

import io.micronaut.http.sse.Event;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import java.util.Collection;

import jakarta.annotation.PostConstruct;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.reactivestreams.Publisher;

/**
 * REST API for To Do list management.
 */
@Controller("/api/tasks")
@Singleton
public class ToDoController
    {
    @Inject
    private TaskRepository tasks;

    @Inject
    private Cluster cluster;

    private Flowable<Event<Task>> flowable;

    @PostConstruct
    void createBroadcaster()
        {
        Observable<Event<Task>> source = Observable.<Event<Task>>create(emitter ->
            {
            TaskRepository.Listener<Task> listener = tasks.listener()
                    .onInsert(task -> emitter.onNext(createEvent("insert", task)))
                    .onUpdate(task -> emitter.onNext(createEvent("update", task)))
                    .onRemove(task -> emitter.onNext(createEvent("delete", task))).build();
            tasks.addListener(listener);
            emitter.setCancellable(() -> tasks.removeListener(listener));
            }).share();
        this.flowable = source.toFlowable(BackpressureStrategy.BUFFER);
        }

    private Event<Task> createEvent(String name, Task task)
        {
        return Event.of(task).name(name);
        }

    @SuppressWarnings("unchecked")
    @Get(value = "/events", produces = MediaType.TEXT_EVENT_STREAM)
    public Publisher<Event<?>> registerEventListener()
        {
        Member        member       = cluster.getLocalMember();
        Event<String> initialEvent = Event.of(member.toString());
        initialEvent.name("begin");

        return Flowable.concatArray(Flowable.fromArray(initialEvent), flowable);
        }

    @Get(produces = MediaType.APPLICATION_JSON)
    public Collection<Task> getTasks(@Nullable @QueryValue(value = "completed") Boolean completed)
        {
        Filter<Task> filter = completed == null
                              ? Filters.always()
                              : Filters.equal(Task::getCompleted, completed);

        return tasks.getAllOrderedBy(filter, Task::getCreatedAt);
        }

    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Task createTask(@Body Task task)
        {
        return tasks.save(new Task(task.getDescription()));
        }

    @Delete("{id}")
    public void deleteTask(@PathVariable("id") String id)
        {
        tasks.removeById(id);
        }

    @Delete
    public void deleteCompletedTasks()
        {
        tasks.deleteByCompletedTrue();
        }

    @Put(value = "{id}", consumes = MediaType.APPLICATION_JSON)
    public Task updateTask(@PathVariable("id") String id, Task task)
        {
        String  description = task.getDescription();
        Boolean completed   = task.getCompleted();

        if (description != null)
            {
            return tasks.update(id, Task::setDescription, description);
            }
        else if (completed != null)
            {
            return tasks.update(id, Task::setCompleted, completed);
            }

        throw new IllegalArgumentException("either description or completion status must be specified");
        }
    }
