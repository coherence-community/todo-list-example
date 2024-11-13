/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
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

import io.helidon.grpc.api.Grpc;

import io.grpc.stub.StreamObserver;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import java.util.stream.Stream;

/**
 * gRPC facade for To Do List API that uses POF marshaller.
 *
 * @author Aleks Seovic  2021.02.28
 */
@Grpc.GrpcService("examples.pof.ToDoList")
@Grpc.GrpcMarshaller("pof")
@ApplicationScoped
public class ToDoListGrpcApiPof
    {
    @Inject
    private ToDoListService api;

    @Inject
    protected TaskRepository tasks;

    // ---- gRPC service API ------------------------------------------------
    
    @Grpc.Unary
    public Task createTask(String description)
        {
        return api.createTask(description);
        }

    @Grpc.ServerStreaming
    public Stream<Task> getAllTasks()
        {
        return api.getTasks(null).stream();
        }

    @Grpc.ServerStreaming
    public Stream<Task> getTasks(boolean completed)
        {
        return api.getTasks(completed).stream();
        }

    @Grpc.Unary
    public Task findTask(String id)
        {
        return api.findTask(id);
        }

    @Grpc.Unary
    public Task deleteTask(String id)
        {
        return api.deleteTask(id);
        }

    @Grpc.Unary
    public boolean deleteCompletedTasks()
        {
        return api.deleteCompletedTasks();
        }

    @Grpc.Unary
    public Task updateDescription(UpdateDescriptionRequest request)
        {
        return api.updateDescription(request.id, request.description);
        }

    @Grpc.Unary
    public Task updateCompletionStatus(UpdateCompletionStatusRequest request)
        {
        return api.updateCompletionStatus(request.id, request.completed);
        }

    @Grpc.ServerStreaming
    public void onInsert(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onInsert(observer::onNext)
                     .build());
        }

    @Grpc.ServerStreaming
    public void onUpdate(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onUpdate(observer::onNext)
                     .build());
        }

    @Grpc.ServerStreaming
    public void onRemove(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onRemove(observer::onNext)
                     .build());
        }

    // ---- request messages ------------------------------------------------

    @PortableType(id = 5000)
    public static class UpdateDescriptionRequest
        {
        @Portable public String id;
        @Portable public String description;
        }

    @PortableType(id = 5001)
    public static class UpdateCompletionStatusRequest
        {
        @Portable public String id;
        @Portable public boolean completed;
        }
    }
