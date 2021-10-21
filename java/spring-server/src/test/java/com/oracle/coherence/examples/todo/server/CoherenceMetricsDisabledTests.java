/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Basic integration test to ensure Coherence metrics are NOT available when
 * property {@code coherence.metrics.http.enabled} is not set.
 *
 * @author Gunnar Hillert
 * @see CoherenceMetricsEnabledTests
 * @see BaseCoherenceMetricsTests
 */
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@DirtiesContext
public class CoherenceMetricsDisabledTests extends BaseCoherenceMetricsTests {

	@Test
	@Order(3)
	void ensureMetricsEndpointIsNotAvailable() {
		assertThrows(
			WebClientRequestException.class, () -> webTestClient.get()
					.uri(COHERENCE_METRICS_URL)
					.exchange());
	}

}
