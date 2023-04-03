package com.oracle.coherence.examples.todo.server.io;

import com.google.protobuf.Empty;
import io.grpc.MethodDescriptor.Marshaller;
import io.helidon.grpc.core.MarshallerSupplier;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

public class JsonMarshaller<T>
        implements Marshaller<T>
    {
    private static final Jsonb JSONB;
    static
        {
        EmptySerializer emptySerializer = new EmptySerializer();
        JsonbConfig config = new JsonbConfig()
                .withSerializers(emptySerializer)
                .withDeserializers(emptySerializer);

        JSONB = JsonbBuilder.create(config);
        }

    private final Class<T> clazz;

    JsonMarshaller(Class<T> clazz)
        {
        this.clazz = clazz;
        }

    public InputStream stream(T obj)
        {
        return new ByteArrayInputStream(JSONB.toJson(obj).getBytes(StandardCharsets.UTF_8));
        }

    public T parse(InputStream in)
        {
        return JSONB.fromJson(in, this.clazz);
        }

    @Named("jsonm")
    @ApplicationScoped
    public static class Supplier
            implements MarshallerSupplier
        {
        public Supplier()
            {
            }

        public <T> Marshaller<T> get(Class<T> clazz)
            {
            return new JsonMarshaller<>(clazz);
            }
        }

    public static class EmptySerializer
            implements JsonbSerializer<Empty>, JsonbDeserializer<Empty>
        {
        @Override
        public void serialize(Empty empty, JsonGenerator generator, SerializationContext serializationContext)
            {
            generator.writeStartObject();
            generator.writeEnd();
            }

        @Override
        public Empty deserialize(JsonParser parser, DeserializationContext deserializationContext, Type type)
            {
            parser.skipObject();
            return Empty.getDefaultInstance();
            }
        }
    }
