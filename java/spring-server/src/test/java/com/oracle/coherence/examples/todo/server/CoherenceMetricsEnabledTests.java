/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.examples.todo.test.common.MetricsAuthentication;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Basic integration test to ensure Coherence metrics are properly exposed when
 * property {@code coherence.metrics.http.enabled} is set to {@code true}.
 *
 * @author Gunnar Hillert
 * @see CoherenceMetricsDisabledTests
 * @see BaseCoherenceMetricsTests
 */
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = {
		"coherence.metrics.http.enabled=true"
	}
)
@AutoConfigureWebTestClient
@DirtiesContext
public class CoherenceMetricsEnabledTests extends BaseCoherenceMetricsTests {

	static {
		MetricsAuthentication.configure();
	}

	@Test
	@Order(3)
	void ensureMetricsEndpointIsAvailable() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL)
				.exchange()
				.expectStatus().isUnauthorized();

		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL)
				.headers(headers -> headers.setBasicAuth(MetricsAuthentication.USER, MetricsAuthentication.PASSWORD))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody().consumeWith(System.out::println);
	}

	@Test
	@Order(4)
	void retrieveSpecificMetrics() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL + "/Coherence.Cache.Size?name=tasks&tier=back")
				.headers(headers -> headers.setBasicAuth(MetricsAuthentication.USER, MetricsAuthentication.PASSWORD))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody().consumeWith(System.out::println)
				.jsonPath("$.length()").isEqualTo(1)
				.jsonPath("$[0].tags.name").isEqualTo("tasks")
				.jsonPath("$[0].value").isEqualTo(1);
	}
}
