package com.oracle.coherence.examples.todo.server.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ToDoControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@Order(1)
	public void addTask() throws Exception {
		mockMvc.perform(
					post("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Collections.singletonMap("description", "hello"))))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@Order(2)
	public void getAllTasks() throws Exception {
		mockMvc.perform(
					get("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(1)))
				.andExpect(jsonPath("$[0].completed", is(false)))
				.andExpect(jsonPath("$[0].description", is("hello")));
	}

	@Test
	@Order(3)
	public void updateNonExistingTask() throws Exception {
		mockMvc.perform(
						put("/api/tasks/{taskId}", 1234)
							.content(objectMapper.writeValueAsString(Collections.singletonMap("description", "hello_updated")))
							.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(4)
	public void optionalGraphQlCompletedArguments() throws Exception {
		JsonNode created = graphQl("mutation { createTask(description: \"optional argument regression\") { id } }")
				.path("createTask");
		String id = created.path("id").asText();
		String quotedId = objectMapper.writeValueAsString(id);
		try {
			for (String argument : new String[] {"", "(completed: null)", "(completed: false)"}) {
				JsonNode tasks = graphQl("{ tasks" + argument + " { id } }").path("tasks");
				boolean found = false;
				for (JsonNode task : tasks) {
					found |= id.equals(task.path("id").asText());
				}
				assertTrue(found, tasks.toString());
			}
			for (String argument : new String[] {"", ", completed: null", ", completed: false"}) {
				JsonNode task = graphQl("mutation { updateTask(id: " + quotedId
						+ ", description: \"updated\"" + argument + ") { id completed description } }")
						.path("updateTask");
				assertFalse(task.path("completed").asBoolean());
				assertEquals("updated", task.path("description").asText());
			}
			JsonNode task = graphQl("mutation { updateTask(id: " + quotedId
					+ ", description: \"completed\", completed: true) { completed } }").path("updateTask");
			assertTrue(task.path("completed").asBoolean());
		}
		finally {
			graphQl("mutation { deleteTask(id: " + quotedId + ") { id } }");
		}
	}

	private JsonNode graphQl(String query) throws Exception {
		String response = mockMvc.perform(post("/graphql")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Collections.singletonMap("query", query))))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JsonNode result = objectMapper.readTree(response);
		assertFalse(result.has("errors"), response);
		return result.path("data");
	}
}
