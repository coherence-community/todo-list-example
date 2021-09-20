/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.repository;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.oracle.coherence.repository.AbstractRepository;

/**
 * A {@code Coherence}-based repository for working with {@link Task tasks}. This
 * implementation uses the Oracle Coherence repository support directly and is NOT
 * using Spring Data.
 * @author Gunnar Hillert
 * @see SpringDataTaskRepository
 */
@Component
@Profile("coherence")
public class TaskRepository extends AbstractRepository<String, Task>
    {

    @CoherenceMap
    private NamedMap<String, Task> tasks;

    @Override
    protected NamedMap<String, Task> getMap()
        {
        return this.tasks;
        }

    @Override
    protected String getId(Task entity)
        {
        return entity.getId();
        }

    @Override
    protected Class<? extends Task> getEntityType()
        {
        return Task.class;
        }
    }
