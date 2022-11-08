/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

/**
 * Task event.
 *
 * @author Aleks Seovic  2020.07.28
 */
public abstract class TaskEvent
    {
    static Annotation INSERTED = new AnnotationLiteral<Inserted>() {};
    static Annotation UPDATED  = new AnnotationLiteral<Updated>() {};
    static Annotation REMOVED  = new AnnotationLiteral<Removed>() {};

    // ---- event types -----------------------------------------------------

    /**
     * A qualifier annotation used for TaskInserted event.
     */
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Inserted
        {
        }

    /**
     * A qualifier annotation used for TaskUpdated event.
     */
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Updated
        {
        }

    /**
     * A qualifier annotation used for TaskRemoved event.
     */
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Removed
        {
        }
    }
