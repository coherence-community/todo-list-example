/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.serializer;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Aleks Seovic  2021.11.04
 * @author Gunnar Hillert
 */
public class MessagePofSerializer<T extends MessageLite>
        implements PofSerializer<T>
    {
    public void serialize(PofWriter out, T msg)
            throws IOException
        {
        out.writeString(0, msg.getClass().getName());
        out.writeByteArray(1, msg.toByteArray());
        out.writeRemainder(null);
        }

    @SuppressWarnings("unchecked")
    public T deserialize(PofReader in) throws IOException
        {
        String sClassName = in.readString(0);
        byte[] abMsg = in.readByteArray(1);
        in.readRemainder();

        try
            {
            Class<? extends MessageLite> clz = (Class<? extends MessageLite>) Class.forName(sClassName);
            Method methodParser = clz.getDeclaredMethod("parser");
            Parser<T> parser = (Parser<T>) methodParser.invoke(clz);
            return parser.parseFrom(abMsg);
            }
        catch (Exception e)
            {
            throw new IOException(e);
            }
        }
    }
