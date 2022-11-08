/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import com.tangosol.io.pof.schema.annotation.Portable;
import com.tangosol.io.pof.schema.annotation.PortableType;
import java.util.UUID;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * A data class representing a single To Do List task.
 *
 * @author Tim Middleton
 * @author Aleks Seovic
 */
@PortableType(id = 1000)
public class Task
    {
    // ---- data members ----------------------------------------------------

    /**
     * The creation time.
     */
    @Portable
    private long createdAt;

    /**
     * The completion status.
     */
    @Portable
    private Boolean completed;

    /**
     * The task ID.
     */
    @Portable
    private String id;

    /**
     * The task description.
     */
    @Portable
    private String description;

    // ---- constructors ----------------------------------------------------

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

    @JsonbCreator
    public Task(@JsonbProperty("id") String id,
                @JsonbProperty("description") String description,
                @JsonbProperty("completed") Boolean completed,
                @JsonbProperty("createdAt") long createdAt)
        {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
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
