/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.netty.channel.DefaultEventLoopGroupFactory;
import io.micronaut.http.netty.channel.NioEventLoopGroupFactory;
import io.micronaut.http.netty.configuration.NettyGlobalConfiguration;

import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

/**
 * Creates Micronaut's default Netty event loop factory from the explicit NIO factory.
 * This works around Micronaut 4.10.x issue 12272, where the map-injection path
 * starts with an empty transport map and fails to find the default {@code nio}
 * transport.
 */
@Factory
public class NettyEventLoopGroupFactory
    {
    @Singleton
    @Primary
    @Replaces(DefaultEventLoopGroupFactory.class)
    DefaultEventLoopGroupFactory defaultEventLoopGroupFactory(NioEventLoopGroupFactory nioFactory,
                                                              @Nullable NettyGlobalConfiguration configuration)
        {
        return new DefaultEventLoopGroupFactory(nioFactory, null, configuration);
        }
    }
