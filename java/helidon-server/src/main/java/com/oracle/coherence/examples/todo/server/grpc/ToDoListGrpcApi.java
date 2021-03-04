package com.oracle.coherence.examples.todo.server.grpc;

import com.oracle.coherence.examples.todo.server.Task;
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

    //----- API methods -----------------------------------------------------

    @ServerStreaming
    public void events(StreamObserver<TaskEvent> observer)
        {
        }

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

    // ---- gRPC messages ---------------------------------------------------

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
    }
