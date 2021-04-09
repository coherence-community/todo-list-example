/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.repository;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.tangosol.net.NamedMap;
import org.springframework.stereotype.Component;
import com.oracle.coherence.repository.AbstractRepository;

import javax.annotation.Resource;

/**
 * A {@code Coherence}-bases repository for working with {@link Task tasks}.
 * @author Gunnar Hillert
 */
@Component
public class TaskRepository extends AbstractRepository<String, Task>
    {

    @Resource(name = "getCache") //TODO - Improve Injection
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
