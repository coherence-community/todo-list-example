package com.oracle.coherence.examples.todo.client;

import com.tangosol.io.pof.schema.annotation.Portable;
import com.tangosol.io.pof.schema.annotation.PortableType;

import io.grpc.stub.StreamObserver;

import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.ServerStreaming;
import io.helidon.microprofile.grpc.core.Unary;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * gRPC client interface for To Do List API
 *
 * @author Aleks Seovic  2021.02.28
 */
@Grpc(name = "examples.pof.ToDoList")
@GrpcMarshaller("pof")
public interface ToDoListGrpcClient
    {
    @Unary
    Task createTask(String description);

    @ServerStreaming
    Stream<Task> getAllTasks();

    @ServerStreaming
    Stream<Task> getTasks(boolean completed);

    @Unary
    Task deleteTask(String id);

    @Unary
    boolean deleteCompletedTasks();

    @Unary
    Task updateDescription(UpdateDescriptionRequest request);

    @Unary
    Task updateCompletionStatus(UpdateCompletionStatusRequest request);

    @ServerStreaming
    void onInsert(StreamObserver<Task> observer);

    @ServerStreaming
    void onUpdate(StreamObserver<Task> observer);

    @ServerStreaming
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
