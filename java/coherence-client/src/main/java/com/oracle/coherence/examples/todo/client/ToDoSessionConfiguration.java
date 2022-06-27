package com.oracle.coherence.examples.todo.client;

import com.oracle.coherence.client.GrpcSessionConfiguration;
import com.tangosol.net.Coherence;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.inject.Produces;

import javax.inject.Singleton;

/**
 * A producer for the client's default {@link GrpcSessionConfiguration}.
 * <p>
 * This is required because the default session normally produced by Coherence
 * is not a gRPC session.
 */
@ApplicationScoped
public class ToDoSessionConfiguration
    {
    @Produces
    @Singleton
    public GrpcSessionConfiguration getConfiguration()
        {
        return GrpcSessionConfiguration
                .builder()
                .withSerializerFormat("pof")
                .build();
        }
    }
