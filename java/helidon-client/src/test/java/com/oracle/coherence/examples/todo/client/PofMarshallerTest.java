/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.client;

import com.google.protobuf.Empty;
import com.oracle.coherence.examples.todo.client.io.PofMarshaller;
import com.tangosol.internal.util.CoherenceMode;
import com.tangosol.io.pof.ConfigurablePofContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PofMarshallerTest
    {
    @Test
    void shouldReadProtobufArraysWithTheDefaultHardenedFilter()
        {
        assertTrue(CoherenceMode.isSecurityHardeningEnabled());
        PofMarshaller<Empty> marshaller = new PofMarshaller<>(
                new ConfigurablePofContext("pof-config.xml"), Empty.class);
        assertEquals(Empty.getDefaultInstance(),
                marshaller.parse(marshaller.stream(Empty.getDefaultInstance())));
        }

    @Test
    void shouldRoundTripAnUpdateRequest()
        {
        PofMarshaller<ToDoListGrpcClient.UpdateDescriptionRequest> marshaller = new PofMarshaller<>(
                new ConfigurablePofContext("pof-config.xml"),
                ToDoListGrpcClient.UpdateDescriptionRequest.class);
        ToDoListGrpcClient.UpdateDescriptionRequest result = marshaller.parse(marshaller.stream(
                new ToDoListGrpcClient.UpdateDescriptionRequest("task-id", "updated")));
        assertEquals("task-id", result.id);
        assertEquals("updated", result.description);
        }
    }
