/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(application = Application.class)
class GraphQLIT
    {
    @Inject
    EmbeddedServer server;

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateQueryUpdateAndDeleteTasks()
        {
        List<String> ids = new ArrayList<>();
        String description = "graphql-" + UUID.randomUUID();
        try
            {
            String completedId = createTask(description, ids);
            String incompleteId = createTask(description + "-incomplete", ids);
            String updatedDescription = description + "-updated";
            query("mutation { updateTask(id: \"" + completedId
                    + "\", completed: true, description: \"" + updatedDescription + "\") { id completed } }");
            List<Map<String, Object>> tasks = (List<Map<String, Object>>) query(
                    "{ tasks(completed: true) { id description completed createdAt createdAtDate } }").get("tasks");
            assertTrue(tasks.stream().anyMatch(task -> completedId.equals(task.get("id"))
                    && updatedDescription.equals(task.get("description"))
                    && Boolean.TRUE.equals(task.get("completed"))));

            tasks = (List<Map<String, Object>>) query(
                    "{ tasks(completed: false) { id completed } }").get("tasks");
            assertTrue(tasks.stream().anyMatch(task -> incompleteId.equals(task.get("id"))
                    && Boolean.FALSE.equals(task.get("completed"))));

            List<Map<String, Object>> remaining = (List<Map<String, Object>>) query(
                    "mutation { deleteCompletedTasks { id } }").get("deleteCompletedTasks");
            assertTrue(remaining.stream().noneMatch(task -> completedId.equals(task.get("id"))));
            assertTrue(remaining.stream().anyMatch(task -> incompleteId.equals(task.get("id"))));

            // Read persisted state independently of the mutation's response.
            tasks = (List<Map<String, Object>>) query("{ tasks { id completed } }").get("tasks");
            assertTrue(tasks.stream().noneMatch(task -> completedId.equals(task.get("id"))),
                    "Bulk deletion must remove the completed task");
            assertTrue(tasks.stream().anyMatch(task -> incompleteId.equals(task.get("id"))
                    && Boolean.FALSE.equals(task.get("completed"))),
                    "Bulk deletion must preserve the incomplete task");

            Map<String, Object> deleted = (Map<String, Object>) query(
                    "mutation { deleteTask(id: \"" + incompleteId + "\") { id } }").get("deleteTask");
            assertEquals(incompleteId, deleted.get("id"));
            tasks = (List<Map<String, Object>>) query("{ tasks { id } }").get("tasks");
            assertTrue(tasks.stream().noneMatch(task -> incompleteId.equals(task.get("id"))));
            }
        finally
            {
            // Attempt every cleanup even if an earlier request fails.
            assertAll(ids.stream().map(id -> () -> given().port(server.getPort()).when()
                    .delete("/api/tasks/" + id).then().statusCode(isOneOf(200, 204))));
            }
        }

    @SuppressWarnings("unchecked")
    private String createTask(String description, List<String> ids)
        {
        Map<String, Object> created = query(
                "mutation { createTask(description: \"" + description + "\") { id } }");
        String id = ((Map<String, String>) created.get("createTask")).get("id");
        ids.add(id);
        return id;
        }

    @SuppressWarnings("unchecked")
    private Map<String, Object> query(String query)
        {
        Map<String, Object> response = given().port(server.getPort()).contentType(JSON)
                .body(Map.of("query", query)).when().post("/graphql")
                .then().statusCode(200).extract().as(Map.class);
        assertNull(response.get("errors"), response.toString());
        return (Map<String, Object>) response.get("data");
        }
    }
