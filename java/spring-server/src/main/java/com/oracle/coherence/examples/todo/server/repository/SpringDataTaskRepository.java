/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.repository;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.spring.data.config.CoherenceMap;
import com.oracle.coherence.spring.data.repository.CoherenceRepository;

/**
 * A {@code Coherence}-based repository for working with {@link Task tasks}. Uses
 * Spring Data support which is provided by Coherence Spring. For an implementation
 * that uses the native Coherence repository support please see: {@link TaskRepository}.
 *
 * @author Gunnar Hillert
 * @see TaskRepository
 */
@CoherenceMap("tasks")
public interface SpringDataTaskRepository extends CoherenceRepository<Task, String> {
}
