/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.runtime.server.EmbeddedServer;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import java.util.Collections;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static io.restassured.http.ContentType.JSON;

import static org.hamcrest.Matchers.is;

/**
 * Basic integration test to ensure Coherence metrics are properly exposed.
 */
@MicronautTest(application=Application.class)
@Property(name = "coherence.metrics.http.enabled", value = "true")
public class MetricsIT
    {
    @Inject
    EmbeddedServer server;

    @Inject
    ApplicationContext ctx;

    @Test
    void shouldBeAbleToObtainMetrics()
        {
        given().
                port(server.getPort()).
                contentType(JSON).
                body(Collections.singletonMap("description", "hello")).
        when().
                post("/api/tasks").
        then().
                log().all().
                statusCode(200).
                body("description", is("hello"),
                     "completed", is(false));

        given().
                port(9612).
                accept(JSON).
        when().
                get("/metrics").
        then().
                statusCode(200);

        given().
                port(9612).
                accept(JSON).
        when().
                get("/metrics/Coherence.Cache.Size?name=tasks&tier=back").
        then().
                log().all().
                statusCode(200).
        assertThat().
                body("size()", is(1)).
                body("[0].tags.name", is("tasks"),
                     "[0].value", is(1));
        }
    }
