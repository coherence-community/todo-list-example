/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import java.util.UUID;

/**
 * A data class representing a single To Do List task.
 *
 * @author Tim Middleton
 * @author Aleks Seovic
 */
public class Task
    {
    // ---- data members ----------------------------------------------------

    /**
     * The creation time.
     */
    private long createdAt;

    /**
     * The completion status.
     */
    private Boolean completed;

    /**
     * The task ID.
     */
    private String id;

    /**
     * The task description.
     */
    private String description;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor.
     */
    public Task()
        {
        }

    /**
     * Construct Task instance.
     *
     * @param description  task description
     */
    public Task(String description)
        {
        this.id = UUID.randomUUID().toString().substring(0, 6);
        this.createdAt = System.currentTimeMillis();
        this.description = description;
        this.completed = false;
        }

    // ---- accessors -------------------------------------------------------

    /**
     * Get the creation time.
     *
     * @return the creation time
     */
    public long getCreatedAt()
        {
        return createdAt;
        }

    /**
     * Get the task ID.
     *
     * @return the task ID
     */
    public String getId()
        {
        return id;
        }

    /**
     * Get the task description.
     *
     * @return the task description
     */
    public String getDescription()
        {
        return description;
        }

    /**
     * Set the task description.
     *
     * @param description  the task description
     */
    public void setDescription(String description)
        {
        this.description = description;
        }

    /**
     * Get the completion status.
     *
     * @return true if it is completed, false otherwise.
     */
    public boolean isCompleted()
        {
        return completed;
        }

    /**
     * Sets the completion status.
     *
     * @param completed  the completion status
     */
    public void setCompleted(boolean completed)
        {
        this.completed = completed;
        }

    // ---- Object methods --------------------------------------------------

    @Override
    public boolean equals(Object object)
        {
        boolean result = false;
        if (object instanceof Task)
            {
            Task todo = (Task) object;
            if (todo.getId() != null && getId() != null)
                {
                result = todo.getId().equals(getId());
                }
            }
        return result;
        }

    @Override
    public int hashCode()
        {
        return id.hashCode();
        }

    @Override
    public String toString()
        {
        return "Task{"
               + "id=" + id
               + ", description=" + description
               + ", completed=" + completed
               + '}';
        }
    }
