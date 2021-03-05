/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.repository.AbstractRepository;

import com.tangosol.net.NamedMap;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import static com.tangosol.util.Filters.always;
import static com.tangosol.util.Filters.equal;

/**
 * Implementation of a Coherence repository that manages access to Tasks.
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

    // ---- finder methods --------------------------------------------------

    public Collection<Task> findByCompletionStatus(Boolean completed)
        {
        var filter = completed == null
                     ? always()
                     : equal(Task::getCompleted, completed);

        return findAll(filter, Task::getCreatedAt);
        }
    }
