/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import io.micronaut.coherence.data.AbstractCoherenceRepository;
import io.micronaut.coherence.data.annotation.CoherenceRepository;
import java.util.Collection;

/**
 * A {@code Coherence}-base {@code Micronaut Data} repository for working with {@link Task tasks}.
 */
@CoherenceRepository("task")
public abstract class TaskRepository
        extends AbstractCoherenceRepository<Task, String>
    {
    /**
     * Find all tasks with the specified completion status.
     *
     * @param completed the task criteria to search for
     *
     * @return a {@link Collection} of {@link Task}s that match
     *         the specified criteria
     */
    public abstract Collection<Task> findByCompleted(boolean completed);

    /**
     * Delete all {@link Task}s that have been completed.
     */
    public abstract void deleteByCompletedTrue();
    }
