/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import com.oracle.coherence.spring.configuration.annotation.EnableCoherence;
import com.oracle.coherence.spring.data.config.EnableCoherenceRepositories;
import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry-point to the To Do Application.
 * @author Gunnar Hillert
 */
@SpringBootApplication
@EnableCoherenceRepositories
public class SpringTodoApplication
    {
    public static void main(String[] args)
        {
        SpringApplication.run(SpringTodoApplication.class, args);
        }

        @Bean
        public GraphQLScalarType longType()
            {
            return Scalars.GraphQLBigInteger;
            }

    }
