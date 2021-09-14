/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Entry-point to the To Do Application.
 * @author Gunnar Hillert
 */
@SpringBootApplication
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
