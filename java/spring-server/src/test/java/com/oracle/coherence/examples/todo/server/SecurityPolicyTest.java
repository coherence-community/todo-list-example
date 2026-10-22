/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.io.json.JsonSerializer;
import com.oracle.coherence.io.json.genson.JsonBindingException;
import com.oracle.coherence.io.json.internal.JsonClassMetadataPolicy;
import com.tangosol.internal.util.CoherenceMode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verify the cross-language Task alias works without weakening the class gate.
 */
class SecurityPolicyTest
    {
    @Test
    void shouldReadTaskAliasInDefaultHardenedMode() throws Exception
        {
        assertTrue(CoherenceMode.isSecurityHardeningEnabled());
        Task task = (Task) new JsonSerializer().deserialize(
                "{\"@class\":\"Task\",\"id\":\"security-test\","
                        + "\"description\":\"hardened\",\"completed\":false,\"createdAt\":1}",
                Object.class);
        assertEquals("security-test", task.getId());
        assertEquals("hardened", task.getDescription());
        }

    @Test
    void shouldRejectAnUnlistedApplicationClass()
        {
        assertTrue(CoherenceMode.isSecurityHardeningEnabled());
        IOException failure = assertThrows(IOException.class, () -> new JsonSerializer().deserialize(
                "{\"@class\":\"" + Unlisted.class.getName() + "\",\"value\":\"denied\"}",
                Object.class));

        Throwable cause = failure;
        while (cause.getCause() != null)
            {
            cause = cause.getCause();
            }
        JsonBindingException rejection = assertInstanceOf(JsonBindingException.class, cause);
        // 26.10 rejects unconfigured @class aliases here, before bean construction.
        // Check the policy origin, without depending on wrapper text or line numbers.
        assertTrue(rejection.getStackTrace().length > 0);
        StackTraceElement origin = rejection.getStackTrace()[0];
        assertEquals(JsonClassMetadataPolicy.class.getName(), origin.getClassName());
        assertEquals("resolveConfiguredAlias", origin.getMethodName());
        }

    public static class Unlisted
        {
        public String value;
        }
    }
