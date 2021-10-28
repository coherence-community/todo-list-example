/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.UUID;

/**
 * A data class representing a single To Do List task.
 *
 * @author Gunnar Hillert
 */
public class Task
        implements Serializable
    {

    // ---- data members ----------------------------------------------------

    /**
     * The task ID.
     */
    @Id
    private String id;

    /**
     * The creation time.
     */
    private Long createdAt;

    /**
     * The completion status.
     */
    private Boolean completed;

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
        this.createdAt = Instant.now().toEpochMilli();
        this.description = description;
        this.completed = false;
        }

    // ---- accessors -------------------------------------------------------

    /**
     * Get the creation time.
     *
     * @return the creation time
     */
    public Long getCreatedAt()
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
    public Boolean getCompleted()
        {
        return completed;
        }

    /**
     * Sets the completion status.
     *
     * @param completed  the completion status
     */
    public void setCompleted(Boolean completed)
        {
        this.completed = completed;
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
    public String toString()
        {
        return "Task{"
               + "id=" + id
               + ", description=" + description
               + ", completed=" + completed
               + '}';
        }
    }
