/*
 * Copyright (c) 2020, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.io.json.GensonBundleProvider;
import com.oracle.coherence.io.json.genson.GensonBuilder;
import com.oracle.coherence.io.json.genson.ext.GensonBundle;

/**
 * JSON serializer configuration.
 */
public class JsonConfig
        implements GensonBundleProvider
    {
    @Override
    public GensonBundle provide()
        {
        return new GensonBundle()
            {
            public void configure(GensonBuilder builder)
                {
                builder.addAlias("Task", Task.class);
                }
            };
        }
    }