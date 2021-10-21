/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import java.util.Collections;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for the Coherence Metrics tests.
 *
 * @author Gunnar Hillert
 * @see CoherenceMetricsDisabledTests
 * @see CoherenceMetricsEnabledTests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseCoherenceMetricsTests {

	public static final int COHERENCE_METRICS_PORT = 9612;
	public static final String COHERENCE_METRICS_URL = "http://localhost:" + COHERENCE_METRICS_PORT + "/metrics";
	public static final String TASKS_URL = "/api/tasks";

	@Autowired
	protected WebTestClient webTestClient;

	@Test
	@Order(1)
	void addATask() {
		webTestClient.post()
				.uri(TASKS_URL)
				.bodyValue(Collections.singletonMap("description", "hello"))
				.exchange()
				.expectStatus().isOk().returnResult(String.class);
	}

	@Test
	@Order(2)
	void getAllTasks() {
		webTestClient.get()
				.uri(TASKS_URL)
				.exchange()
				.expectStatus().isOk()
				.expectBody().consumeWith(System.out::println)
				.jsonPath("$[0].description").isEqualTo("hello")
				.jsonPath("$[0].completed").isEqualTo(false).returnResult();
	}

}
