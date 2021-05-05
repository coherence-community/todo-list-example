/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.repository;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.repository.AbstractRepository;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.oracle.coherence.spring.data.repository.CoherenceRepository;
import com.tangosol.net.NamedMap;
import com.tangosol.util.UUID;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * A {@code Coherence}-bases repository for working with {@link Task tasks}.
 * @author Gunnar Hillert
 */
@com.oracle.coherence.spring.data.config.CoherenceMap("tasks")
public interface SpringDataTaskRepository extends CoherenceRepository<Task, String> {
}

