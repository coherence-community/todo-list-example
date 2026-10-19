/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.client;

import com.google.protobuf.Empty;
import com.oracle.coherence.examples.todo.client.io.PofMarshaller;
import com.tangosol.io.pof.ConfigurablePofContext;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import io.helidon.config.Config;
import io.helidon.microprofile.grpc.client.GrpcChannelDescriptor;
import io.helidon.webclient.grpc.GrpcClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercise the JavaFX client's POF marshallers against a separate Helidon server.
 * Run with -Dtodo.helidon.integration=true after starting a disposable Helidon server.
 */
@EnabledIfSystemProperty(named = "todo.helidon.integration", matches = "true")
class GrpcClientIT
    {
    @Test
    void shouldCreateUpdateFilterDeleteAndReceiveEvents() throws Exception
        {
        GrpcChannelDescriptor connection = GrpcChannelDescriptor.builder()
                .config(Config.create().get("grpc.client.channels").asNodeList().get().getFirst())
                .build();
        Channel channel = GrpcClient.builder()
                .baseUri("http://" + connection.host() + ":" + connection.port())
                .tls(connection.tls().orElseThrow())
                .build().channel();
        ConfigurablePofContext pof = new ConfigurablePofContext("pof-config.xml");
        CallOptions options = CallOptions.DEFAULT.withDeadlineAfter(30, TimeUnit.SECONDS);
        CountDownLatch inserted = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        String description = "java-grpc-qualification-" + java.util.UUID.randomUUID();
        ClientCall<Empty, Task> events = channel.newCall(method(pof, "onInsert",
                Empty.class, Task.class, MethodDescriptor.MethodType.SERVER_STREAMING), options);
        ClientCalls.asyncServerStreamingCall(events, Empty.getDefaultInstance(), new StreamObserver<>()
            {
            public void onNext(Task task)
                {
                if (description.equals(task.getDescription()))
                    {
                    inserted.countDown();
                    }
                }

            public void onError(Throwable error)
                {
                failure.set(error);
                inserted.countDown();
                }

            public void onCompleted()
                {
                }
            });
        String id = null;
        try
            {
            Thread.sleep(500);
            Task task = ClientCalls.blockingUnaryCall(channel,
                    method(pof, "createTask", String.class, Task.class, MethodDescriptor.MethodType.UNARY),
                    options, description);
            id = task.getId();
            assertEquals(description, task.getDescription());
            assertTrue(inserted.await(10, TimeUnit.SECONDS), "No insert event received");
            assertEquals(null, failure.get(), "Event stream failed");

            task = ClientCalls.blockingUnaryCall(channel,
                    method(pof, "updateDescription", ToDoListGrpcClient.UpdateDescriptionRequest.class,
                            Task.class, MethodDescriptor.MethodType.UNARY), options,
                    new ToDoListGrpcClient.UpdateDescriptionRequest(id, description + "-updated"));
            assertEquals(description + "-updated", task.getDescription());
            task = ClientCalls.blockingUnaryCall(channel,
                    method(pof, "updateCompletionStatus", ToDoListGrpcClient.UpdateCompletionStatusRequest.class,
                            Task.class, MethodDescriptor.MethodType.UNARY), options,
                    new ToDoListGrpcClient.UpdateCompletionStatusRequest(id, true));
            assertTrue(task.isCompleted());

            Iterator<Task> completed = ClientCalls.blockingServerStreamingCall(channel,
                    method(pof, "getTasks", Boolean.class, Task.class,
                            MethodDescriptor.MethodType.SERVER_STREAMING), options, true);
            boolean found = false;
            while (completed.hasNext())
                {
                found |= id.equals(completed.next().getId());
                }
            assertTrue(found, "Completed task was not returned by the server filter");
            }
        finally
            {
            events.cancel("Test complete", null);
            if (id != null)
                {
                ClientCalls.blockingUnaryCall(channel,
                        method(pof, "deleteTask", String.class, Task.class,
                                MethodDescriptor.MethodType.UNARY), options, id);
                }
            }
        }

    private static <Req, Resp> MethodDescriptor<Req, Resp> method(ConfigurablePofContext pof,
            String name, Class<Req> request, Class<Resp> response, MethodDescriptor.MethodType type)
        {
        return MethodDescriptor.<Req, Resp>newBuilder()
                .setFullMethodName("examples.pof.ToDoList/" + name)
                .setType(type)
                .setRequestMarshaller(new PofMarshaller<>(pof, request))
                .setResponseMarshaller(new PofMarshaller<>(pof, response))
                .build();
        }
    }
