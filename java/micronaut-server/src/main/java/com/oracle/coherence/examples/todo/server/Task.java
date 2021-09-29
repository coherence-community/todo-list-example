/*
 * Copyright (c) 2020, 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.repository.Indexed;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.io.Serializable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Objects;
import java.util.UUID;

/**
 * A data class representing a single To Do List task.
 *
 * @author Tim Middleton
 * @author Aleks Seovic
 */
@MappedEntity
public class Task
        implements Serializable
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
    @Id
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
     * Construct {@link Task} instance.
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
     *
     * @return this task
     */
    public Task setDescription(String description)
        {
        this.description = description;
        return this;
        }

    /**
     * Get the completion status.
     *
     * @return true if it is completed, false otherwise.
     */
    @Indexed
    public Boolean getCompleted()
        {
        return completed;
        }

    /**
     * Sets the completion status.
     *
     * @param completed  the completion status
     *
     * @return this task
     */
    public Task setCompleted(Boolean completed)
        {
        this.completed = completed;
        return this;
        }

    /**
     * Returns the created date as a {@link LocalDateTime}.
     *
     * @return the created date as a {@link LocalDateTime}.
     */
    public LocalDateTime getCreatedAtDate()
        {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault());
        }

    // ---- Object methods --------------------------------------------------

    @Override
    public boolean equals(Object o)
        {
        if (this == o)
            {
            return true;
            }
        if (o == null || getClass() != o.getClass())
            {
            return false;
            }
        Task task = (Task) o;
        return createdAt == task.createdAt &&
               Objects.equals(completed, task.completed) &&
               id.equals(task.id) &&
               description.equals(task.description);
        }

    @Override
    public int hashCode()
        {
        return Objects.hash(createdAt, completed, id, description);
        }

    @Override
    public String toString()
        {
        return "Task{"
               + "id=" + id
               + ", description=" + description
               + ", completed=" + completed
               + ", createdAt=" + getCreatedAtDate()
               + '}';
        }
    }
