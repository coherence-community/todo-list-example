/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.client;

import com.tangosol.io.pof.schema.annotation.Portable;
import com.tangosol.io.pof.schema.annotation.PortableType;

import io.grpc.stub.StreamObserver;

import io.helidon.grpc.api.Grpc;

import java.util.stream.Stream;

/**
 * gRPC client interface for To Do List API
 *
 * @author Aleks Seovic  2021.02.28
 */
@Grpc.GrpcService("examples.pof.ToDoList")
@Grpc.GrpcMarshaller("pof")
public interface ToDoListGrpcClient
    {
    @Grpc.Unary
    Task createTask(String description);

    @Grpc.ServerStreaming
    Stream<Task> getAllTasks();

    @Grpc.ServerStreaming
    Stream<Task> getTasks(boolean completed);

    @Grpc.Unary
    Task deleteTask(String id);

    @Grpc.Unary
    boolean deleteCompletedTasks();

    @Grpc.Unary
    Task updateDescription(UpdateDescriptionRequest request);

    @Grpc.Unary
    Task updateCompletionStatus(UpdateCompletionStatusRequest request);

    @Grpc.ServerStreaming
    void onInsert(StreamObserver<Task> observer);

    @Grpc.ServerStreaming
    void onUpdate(StreamObserver<Task> observer);

    @Grpc.ServerStreaming
    void onRemove(StreamObserver<Task> observer);

    // ---- request messages ------------------------------------------------

    @PortableType(id = 5000)
    class UpdateDescriptionRequest
        {
        @Portable public String id;
        @Portable public String description;

        public UpdateDescriptionRequest(String id, String description)
            {
            this.id = id;
            this.description = description;
            }
        }

    @PortableType(id = 5001)
    class UpdateCompletionStatusRequest
        {
        @Portable public String id;
        @Portable public boolean completed;

        public UpdateCompletionStatusRequest(String id, boolean completed)
            {
            this.id = id;
            this.completed = completed;
            }
        }
    }
