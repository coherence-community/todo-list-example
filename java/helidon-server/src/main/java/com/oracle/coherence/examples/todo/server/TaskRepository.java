/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.repository.AbstractRepository;

import com.tangosol.net.NamedMap;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

/**
 * Implementation of a Coherence repository that manages access to Tasks.
 *
 * @author Aleks Seovic  2021.02.27
 */
@ApplicationScoped
public class TaskRepository extends AbstractRepository<String, Task>
    {
    @Inject
    private NamedMap<String, Task> tasks;

    protected NamedMap<String, Task> getMap()
        {
        return tasks;
        }

    protected String getId(Task task)
        {
        return task.getId();
        }

    protected Class<? extends Task> getEntityType()
        {
        return Task.class;
        }
    }
