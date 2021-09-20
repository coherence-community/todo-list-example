/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.server.graphql;

import com.oracle.coherence.examples.todo.server.Task;
import com.tangosol.net.NamedMap;

import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.Processors;

import graphql.GraphQL;

import graphql.language.StringValue;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import io.micronaut.coherence.annotation.Name;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import io.micronaut.core.io.ResourceResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * Various bean factories for {@code GraphQL} {@link DataFetcher}s.
 */
@Factory
public class GraphQLFactory
    {
    @Bean
    @Singleton
    @Inject
    public GraphQL graphQL(ResourceResolver resourceResolver,
                           @Named("createTask") DataFetcher<Collection<Task>> createTaskFetcher,
                           @Named("deleteCompletedTasks") DataFetcher<Task> deleteCompletedTasksFetcher,
                           @Named("deleteTask") DataFetcher<Task> deleteTaskFetcher,
                           @Named("updateTask") DataFetcher<Task> updateTaskFetcher,
                           @Named("findTask") DataFetcher<Task> findTaskFetcher,
                           @Named("tasks") DataFetcher<Collection<Task>> tasksFetcher)
        {
        // Parse the schema.
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        SchemaParser schemaParser = new SchemaParser();
        typeRegistry.merge(schemaParser.parse(
                new BufferedReader(new InputStreamReader(resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()))));

        // Create the runtime wiring.
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .scalar(LOCAL_DATE_TIME)
                .type("Query", typeWiring -> typeWiring.dataFetcher("findTask", findTaskFetcher))
                .type("Query", typeWiring -> typeWiring.dataFetcher("tasks", tasksFetcher))
                .type("Mutation", typeWiring -> typeWiring.dataFetcher("createTask", createTaskFetcher))
                .type("Mutation", typeWiring -> typeWiring.dataFetcher("deleteCompletedTasks", deleteCompletedTasksFetcher))
                .type("Mutation", typeWiring -> typeWiring.dataFetcher("deleteTask", deleteTaskFetcher))
                .type("Mutation", typeWiring -> typeWiring.dataFetcher("updateTask", updateTaskFetcher))
                .build();

        // Create the executable schema.
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

        // Return the GraphQL bean.
        return GraphQL.newGraphQL(graphQLSchema).build();
        }


    @Bean
    @Singleton
    @Named("createTask")
    public DataFetcher<Task> createTasksFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            String description = environment.getArgument("description");
            Objects.requireNonNull(description, "Description must be provided");
            Task task = new Task(description);
            tasks.put(task.getId(), task);
            return task;
            };
        }

    @Bean
    @Singleton
    @Named("deleteCompletedTasks")
    public DataFetcher<Collection<Task>> deleteCompletedTasksFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            tasks.invokeAll(Filters.equal(Task::getCompleted, true), Processors.remove(Filters.always()));
            return tasks.values();
            };
        }

    @Bean
    @Singleton
    @Named("deleteTask")
    public DataFetcher<Task> deleteTaskFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            String id = environment.getArgument("id");
            return Optional.ofNullable(tasks.remove(id))
                    .orElseThrow(() -> new TaskNotFoundException(GraphQLFactory.MESSAGE + id));
            };
        }

    @Bean
    @Singleton
    @Named("findTask")
    public DataFetcher<Task> findTasksFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            String id = environment.getArgument("id");
            return Optional.ofNullable(tasks.get(id))
                    .orElseThrow(() -> new TaskNotFoundException(GraphQLFactory.MESSAGE + id));
            };
        }

    @Bean
    @Singleton
    @Named("tasks")
    public DataFetcher<Collection<Task>> tasksFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            Boolean completed = environment.<Boolean>getArgument("completed");
            Filter<Task> filter = completed == null
                                  ? Filters.always()
                                  : Filters.equal(Task::getCompleted, completed);

            return tasks.values(filter, Comparator.comparingLong(Task::getCreatedAt));
            };
        }

    @Bean
    @Singleton
    @Named("updateTask")
    public DataFetcher<Task> updateTaskFetcher(@Name("tasks") NamedMap<String, Task> tasks)
        {
        return environment ->
            {
            String id = environment.getArgument("id");
            String description = environment.getArgument("description");
            Boolean completed = environment.getArgument("completed");
            try
                {
                return tasks.compute(id, (k, v) ->
                    {
                    Objects.requireNonNull(v);

                    if (description != null)
                        {
                        v.setDescription(description);
                        }
                    if (completed != null)
                        {
                        v.setCompleted(completed);
                        }
                    return v;
                    });
                }
            catch (Exception e)
                {
                throw new TaskNotFoundException(GraphQLFactory.MESSAGE + id);
                }
            };
        }

    private static final String MESSAGE = "Unable to find task with id ";

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();


    // TODO: replace with graphql-java-extended-scalars once it become possible to use graphql-java 15+
    public static final GraphQLScalarType LOCAL_DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("A custom scalar that handles local date time")
            .coercing(new Coercing<LocalDateTime, String>()
                {
                @Override
                public String serialize(Object input)
                    {
                    LocalDateTime localDateTime;
                    if (input instanceof LocalDateTime)
                        {
                        localDateTime = (LocalDateTime) input;
                        }
                    else if (input instanceof String)
                        {
                        localDateTime = parseLocalDateTime(input.toString(), CoercingSerializeException::new);
                        }
                    else
                        {
                        throw new CoercingSerializeException(
                                "Expected something we can convert to 'java.time.LocalDateTime' but was '" + input.getClass() + "'.");
                        }
                    try
                        {
                        return LOCAL_DATE_TIME_FORMATTER.format(localDateTime);
                        }
                    catch (DateTimeException e)
                        {
                        throw new CoercingSerializeException(
                                "Unable to turn TemporalAccessor into LocalDateTime because of : '" + e.getMessage() + "'."
                        );
                        }
                    }

                @Override
                public LocalDateTime parseValue(Object input)
                    {
                    LocalDateTime localDateTime;
                    if (input instanceof LocalDateTime)
                        {
                        localDateTime = (LocalDateTime) input;
                        }
                    else if (input instanceof String)
                        {
                        localDateTime = parseLocalDateTime(input.toString(), CoercingParseValueException::new);
                        }
                    else
                        {
                        throw new CoercingParseValueException("Expected a 'String' but was '" + "typeName(input)" + "'.");
                        }
                    return localDateTime;
                    }

                private LocalDateTime parseLocalDateTime(String s, Function<String, RuntimeException> exceptionMaker)
                    {
                    try
                        {
                        return LocalDateTime.parse(s, LOCAL_DATE_TIME_FORMATTER);
                        }
                    catch (DateTimeParseException e)
                        {
                        throw exceptionMaker.apply("Invalid RFC3339 value : '" + s + "'. because of : '" + e.getMessage() + "'");
                        }
                    }

                @Override
                public LocalDateTime parseLiteral(Object input)
                    {
                    if (!(input instanceof StringValue))
                        {
                        throw new CoercingParseLiteralException(
                                "Expected AST type 'StringValue' but was '" + input.getClass() + "'.");
                        }
                    return parseLocalDateTime(((StringValue) input).getValue(), CoercingParseLiteralException::new);
                    }
                })
            .build();

    /**
     * An exception indicating that a {@link Task} was not found.
     */
    public static class TaskNotFoundException
            extends Exception
        {
        /**
         * Create the exception.
         *
         * @param message reason for the exception.
         */
        public TaskNotFoundException(String message)
            {
            super(message);
            }
        }
    }
