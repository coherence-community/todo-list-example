/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.client;

import com.oracle.bedrock.testsupport.deferred.Eventually;
import com.oracle.bedrock.testsupport.junit.TestLogsExtension;
import com.oracle.coherence.cdi.CoherenceExtension;
import javafx.fxml.FXMLLoader;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.DockerHealthcheckWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.inject.Inject;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

/**
 * These integration tests start a To-Do Server in a container and
 * run various {@link TaskManager} methods.
 */
@Testcontainers
public class TaskManagerIT
    {
    /**
     * The {@link TestLogsExtension} is used to write the logs from
     * the Docker container to files under target/test-output.
     */
    @RegisterExtension()
    @Order(0)
    static final TestLogsExtension logs = new TestLogsExtension(TaskManagerIT.class);

    static final String DEFAULT_IMAGE = "ghcr.io/coherence-community/todo-list-coherence-server:latest";

    /**
     * The image name to use.
     */
    static final String IMAGE = System.getProperty("coherence.image", DEFAULT_IMAGE);
    /**
     * Uses Testcontainers to run a single to-do application Coherence server.
     */
    @Container
    @Order(1)
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final GenericContainer coherence = new GenericContainer(DockerImageName.parse(IMAGE))
            .withExposedPorts(1408)
            .waitingFor(new DockerHealthcheckWaitStrategy())
            .withLogConsumer(new ConsoleLogConsumer(logs.build("server-container")))
            .withEnv("COHERENCE_LOG_LEVEL", "9")
            .withEnv("COHERENCE_CLUSTER", "ToDo")
            .withEnv("COHERENCE_ROLE", "storage");

    /**
     * Sets system properties, specifically the port the container has mapped the
     * Coherence gRPC port to.
     */
    @RegisterExtension
    @Order(2)
    static final SystemPropertyExtension properties = new SystemPropertyExtension()
            .withProperty("coherence.grpc.port", () -> coherence.getMappedPort(1408))
            .withProperty("coherence.grpc.address", () -> "127.0.0.1")
            .withProperty("coherence.profile", () -> "thin")
            .withProperty("coherence.pof.enabled", () -> "true")
            .withProperty("coherence.wka", () -> "127.0.0.1")
            .withProperty("coherence.client", () -> "grpc-fixed");

    /**
     * Set's up the Weld JUnit extension.
     * This is done with {@link RegisterExtension} rather than using {@link org.junit.jupiter.api.extension.ExtendWith}
     * because Weld must be set-up AFTER the server container has started and the gRPC port system property has been set.
     */
    @RegisterExtension
    @Order(3)
    static final WeldJunit5Extension weldExtension = new WeldJunit5Extension();

    /**
     * Initialise Weld
     */
    @SuppressWarnings("unused")
    @WeldSetup
    private final WeldInitiator weld = WeldInitiator.of(WeldInitiator.createWeld()
            .addPackages(TaskManager.class)
            .addPackages(FXMLLoader.class)
            .addPackages(CoherenceExtension.class)
            .addExtension(new CoherenceExtension()));

    /**
     * The {@link TaskManager} injected by Weld.
     */
    @Inject
    TaskManager taskManager;

    @Test
    public void shouldCreateTasks()
        {
        assertThat(taskManager.getAllTasks(), is(empty()));
        assertThat(taskManager.getActiveTasks(), is(empty()));
        assertThat(taskManager.getActiveCount(), is(0));
        assertThat(taskManager.getCompletedTasks(), is(empty()));
        assertThat(taskManager.getCompletedCount(), is(0));

        taskManager.addTodo("Do something");

        Eventually.assertDeferred(taskManager::getActiveCount, is(1));
        assertThat(taskManager.getCompletedCount(), is(0));

        assertThat(taskManager.getCompletedTasks(), is(empty()));

        Collection<Task> allTasks = taskManager.getAllTasks();
        Collection<Task> activeTasks = taskManager.getActiveTasks();
        assertThat(allTasks.size(), is(1));
        assertThat(activeTasks.size(), is(1));

        Task task = allTasks.stream().findAny().orElse(null);
        assertThat(task, is(notNullValue()));
        assertThat(task.getDescription(), is("Do something"));
        assertThat(task.isCompleted(), is(false));

        Task taskActive = activeTasks.stream().findAny().orElse(null);
        assertThat(taskActive, is(notNullValue()));
        assertThat(taskActive.getDescription(), is("Do something"));
        assertThat(taskActive.isCompleted(), is(false));
        assertThat(taskActive, is(task));

        taskManager.updateText(task.getId(), "Do something else");

        allTasks = taskManager.getAllTasks();
        activeTasks = taskManager.getActiveTasks();
        assertThat(allTasks.size(), is(1));
        assertThat(activeTasks.size(), is(1));

        task = allTasks.stream().findAny().orElse(null);
        assertThat(task, is(notNullValue()));
        assertThat(task.getDescription(), is("Do something else"));
        assertThat(task.isCompleted(), is(false));

        taskActive = activeTasks.stream().findAny().orElse(null);
        assertThat(taskActive, is(notNullValue()));
        assertThat(taskActive.getDescription(), is("Do something else"));
        assertThat(taskActive.isCompleted(), is(false));

        taskManager.updateCompleted(task.getId(), true);

        assertThat(taskManager.getActiveCount(), is(0));
        assertThat(taskManager.getCompletedCount(), is(1));

        Collection<Task> completedTasks = taskManager.getCompletedTasks();
        assertThat(completedTasks, is(notNullValue()));
        assertThat(completedTasks.size(), is(1));
        Task taskCompleted = completedTasks.stream().findAny().orElse(null);
        assertThat(taskCompleted, is(notNullValue()));
        assertThat(taskCompleted.getDescription(), is("Do something else"));
        assertThat(taskCompleted.isCompleted(), is(true));

        taskManager.removeCompletedTasks();
        assertThat(taskManager.getAllTasks(), is(empty()));
        assertThat(taskManager.getActiveTasks(), is(empty()));
        assertThat(taskManager.getCompletedTasks(), is(empty()));
        }
    }
