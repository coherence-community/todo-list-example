/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.tangosol.net.Cluster;
import com.tangosol.net.Member;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;

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
    @Get("/events")
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Publisher<Event<?>> registerEventListener()
        {
        Member member = cluster.getLocalMember();
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

        return tasks.getAllOrderedBy(filter, Task::getCreatedAt);
        }

    @Post
    @Consumes(APPLICATION_JSON)
    public void createTask(Task task)
        {
        tasks.save(new Task(task.getDescription()));
        }

    @Delete("{id}")
    public void deleteTask(@PathVariable("id") String id)
        {
        tasks.removeById(id);
        }

    @Delete
    public void deleteCompletedTasks()
        {
        tasks.removeAll(Filters.equal(Task::getCompleted, true), false);
        }

    @Put("{id}")
    @Consumes(APPLICATION_JSON)
    public Task updateTask(@PathVariable("id") String id, Task task)
        {
        String description = task.getDescription();
        Boolean completed = task.getCompleted();

        return tasks.update(id, tsk ->
            {
            Objects.requireNonNull(tsk);

            if (description != null)
                {
                tsk.setDescription(description);
                }
            if (completed != null)
                {
                tsk.setCompleted(completed);
                }
            return tsk;
            });
        }
    }
