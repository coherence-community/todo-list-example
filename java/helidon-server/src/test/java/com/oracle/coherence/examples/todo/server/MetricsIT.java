package com.oracle.coherence.examples.todo.server;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;

public class MetricsIT {
	protected static Server SERVER;

	@BeforeAll
	static void startServer() {
		SERVER = Server.builder().port(0).build().start();
	}

	@AfterAll
	static void stopServer() {
		SERVER.stop();
	}

	@BeforeEach
	void setup() {
		RestAssured.reset();
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	void testCreateTask() {
		given().
				port(SERVER.port()).
				contentType(JSON).
				body(Collections.singletonMap("description", "hello")).
		when().
				post("/api/tasks").
		then().
				log().all().
				statusCode(OK.getStatusCode()).
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
