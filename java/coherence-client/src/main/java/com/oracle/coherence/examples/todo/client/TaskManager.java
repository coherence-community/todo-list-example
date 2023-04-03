/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import com.oracle.coherence.cdi.Scope;
import com.oracle.coherence.cdi.SessionInitializer;
import com.oracle.coherence.cdi.events.MapName;

import com.tangosol.net.Coherence;
import com.tangosol.net.NamedMap;

import com.tangosol.util.Aggregators;
import com.tangosol.util.Filters;
import com.tangosol.util.MapEvent;
import com.tangosol.util.Processors;
import com.tangosol.util.Filter;

import java.util.Collection;

import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;

import jakarta.inject.Inject;
import jakarta.inject.Named;


/**
 * The client used to interact with Coherence NamedMap.
 *
 * @author Tim Middleton
 * @author Manfred Riem
 * @author Aleks Seovic
 */
@ApplicationScoped
public class TaskManager
    {
    /**
     * A {@link Filter} to retrieve completed tasks.
     */
    private static final Filter<Task> COMPLETED = 
            Filters.equal("completed", true);

    /**
     * A {@link Filter} to retrieve active tasks.
     */
    private static final Filter<Task> ACTIVE = 
            Filters.equal("completed", false);

    @ApplicationScoped
    @Named("")
    @Scope("")
    public static class ClientSessionBean
            implements SessionInitializer
        {
        public Optional<Coherence.Mode> getMode()
            {
            return Optional.of(Coherence.Mode.GrpcFixed);
            }
        }

    /**
     * Tasks map.
     */
    @Inject
    private NamedMap<String, Task> tasks;

    @Inject
    private Event<TaskEvent> taskEvent;

    /**
     * Convert Coherence map events to CDI events.
     */
    void onTaskEvent(@Observes @MapName("tasks") MapEvent<String, Task> event)
        {
        taskEvent.fire(new TaskEvent(event.getOldValue(), event.getNewValue()));
        }

    /**
     * Add a task with the given description.
     *
     * @param description  the task description
     */
    public void addTodo(String description)
        {
        Task todo = new Task(description);
        tasks.put(todo.getId(), todo);
        }

    /**
     * Get the number of active tasks.
     *
     * @return the number of active tasks
     */
    public int getActiveCount()
        {
        return tasks.aggregate(ACTIVE, Aggregators.count());
        }

    /**
     * Get the number of completed tasks.
     *
     * @return the number of completed tasks
     */
    public int getCompletedCount()
        {
        return tasks.aggregate(COMPLETED, Aggregators.count());
        }

    /**
     * Get all the tasks.
     *
     * @return all the tasks
     */
    public Collection<Task> getAllTasks()
        {
        return tasks.values();
        }

    /**
     * Get the active tasks.
     *
     * @return the active tasks
     */
    public Collection<Task> getActiveTasks()
        {
        return tasks.values(ACTIVE);
        }

    /**
     * Get the completed tasks.
     *
     * @return the completed tasks
     */
    public Collection<Task> getCompletedTasks()
        {
        return tasks.values(COMPLETED);
        }

    /**
     * Remove all completed tasks.
     */
    public void removeCompletedTasks()
        {
        tasks.invokeAll(COMPLETED, Processors.remove(Filters.always()));
        }

    /**
     * Remove a single task.
     *
     * @param id  the task ID
     */
    public void removeTodo(String id)
        {
        tasks.remove(id);
        }

    /**
     * Update the completion status of a task.
     *
     * @param id         the task ID
     * @param completed  the completion status to set
     */
    public void updateCompleted(String id, Boolean completed)
        {
        tasks.invoke(id, Processors.update("setCompleted", completed));
        }

    /**
     * Update the description of a task.
     *
     * @param id           the task ID
     * @param description  the task description
     */
    public void updateText(String id, String description)
        {
        tasks.invoke(id, Processors.update("setDescription", description));
        }
    }
