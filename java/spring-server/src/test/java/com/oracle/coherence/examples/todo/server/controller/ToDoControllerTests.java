package com.oracle.coherence.examples.todo.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

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
}
