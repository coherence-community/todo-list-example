/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.grpc;

import com.oracle.coherence.examples.todo.server.Task;
import com.oracle.coherence.examples.todo.server.TaskRepository;
import com.oracle.coherence.examples.todo.server.ToDoListService;

import com.tangosol.io.pof.schema.annotation.Portable;
import com.tangosol.io.pof.schema.annotation.PortableType;

import io.grpc.stub.StreamObserver;

import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.ServerStreaming;
import io.helidon.microprofile.grpc.core.Unary;

import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

/**
 * gRPC facade for To Do List API
 */
@Grpc(name = "examples.ToDoList")
@GrpcMarshaller("pof")
@ApplicationScoped
public class ToDoListGrpcApi
    {
    @Inject
    private ToDoListService api;

    @Inject
    protected TaskRepository tasks;

    @Unary
    public Task createTask(String description)
        {
        return api.createTask(description);
        }

    @ServerStreaming
    public Stream<Task> getTasks(Boolean completed)
        {
        return api.getTasks(completed).stream();
        }

    @Unary
    public Task findTask(String id)
        {
        return api.findTask(id);
        }

    @Unary
    public Task deleteTask(String id)
        {
        return api.deleteTask(id);
        }

    @Unary
    public boolean deleteCompletedTasks()
        {
        return api.deleteCompletedTasks();
        }

    @Unary
    public Task updateDescription(UpdateDescriptionRequest request)
        {
        return api.updateDescription(request.id, request.description);
        }

    @Unary
    public Task updateCompletionStatus(UpdateCompletionStatusRequest request)
        {
        return api.updateCompletionStatus(request.id, request.completed);
        }

    @ServerStreaming
    public void events(StreamObserver<TaskEvent> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onInsert(task -> observer.onNext(new TaskInserted(task)))
                     .onUpdate(task -> observer.onNext(new TaskUpdated(task)))
                     .onRemove(task -> observer.onNext(new TaskRemoved(task)))
                     .build());
        }

    @PortableType(id = 5000)
    public static class UpdateDescriptionRequest
        {
        @Portable String id;
        @Portable String description;
        }

    @PortableType(id = 5001)
    public static class UpdateCompletionStatusRequest
        {
        @Portable String id;
        @Portable boolean completed;
        }

    public interface TaskEvent
        {}

    @PortableType(id = 5100)
    public static class TaskInserted implements TaskEvent
        {
        @Portable Task task;

        public TaskInserted(Task task)
            {
            this.task = task;
            }
        }

    @PortableType(id = 5101)
    public static class TaskUpdated implements TaskEvent
        {
        @Portable Task task;

        public TaskUpdated(Task task)
            {
            this.task = task;
            }
        }

    @PortableType(id = 5102)
    public static class TaskRemoved implements TaskEvent
        {
        @Portable Task task;

        public TaskRemoved(Task task)
            {
            this.task = task;
            }
        }
    }
