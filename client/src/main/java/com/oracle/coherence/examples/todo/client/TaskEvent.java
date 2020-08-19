/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

/**
 * Task event.
 *
 * @author Aleks Seovic  2020.07.28
 */
public class TaskEvent
    {
    private final Task oldValue;
    private final Task newValue;

    public TaskEvent(Task oldValue, Task newValue)
        {
        this.oldValue = oldValue;
        this.newValue = newValue;
        }

    public Task getOldValue()
        {
        return oldValue;
        }

    public Task getNewValue()
        {
        return newValue;
        }
    }
