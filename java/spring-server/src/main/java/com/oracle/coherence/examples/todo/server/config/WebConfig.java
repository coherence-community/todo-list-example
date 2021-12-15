/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.config;

import com.zhokhov.graphql.datetime.LocalDateTimeScalar;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Gunnar Hillert
 */
@Configuration(proxyBeanMethods = false)
public class WebConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer()
	{
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("*");
			}
		};
	}

	@Bean
	RuntimeWiringConfigurer runtimeWiringConfigurer() {
		return (wiringBuilder) -> wiringBuilder
				.scalar(ExtendedScalars.GraphQLBigInteger)
				.scalar(LocalDateTimeScalar.create("LocalDateTime", false, null));
	}
}
