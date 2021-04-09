# Coherence To Do List Example Application

## Overview

### Clients

This example is a simple task management application which allows users to add, complete,
filter and delete tasks within a list.

The example currently provides four clients to work with tasks:

1. [React](https://reactjs.org/) front-end integrating with one of the server back-end integrated 
   with Coherence using the `coherence-cdi` and `coherence-mp` modules.

2. [GraphQL](https://graphql.org/) end-point with optional GraphiQL UI using Helidon Microprofile GraphQL. 
   See [here](graphql.md) for instructions on how to access the GraphQL endpoint.

3. [JavaFX](https://openjfx.io/) client connecting as a Coherence Java client 

4. [Oracle JET](https://www.oracle.com/webfolder/technetwork/jet/index.html) front-end (Optional)

Any number of the clients can be run and will receive all events from other clients as
tasks are created, updated, completed or removed. This is achieved using _Server Sent Events_
(SSE) for the React client and _Coherence Live Events_ for the JavaFX client.

### Server Implementations

Three server implementation are provided by the following Maven modules:

- **helidon-server** Provides a [Helidon](https://helidon.io/)-powered server
- **micronaut-server** Provides a [Micronaut](https://micronaut.io/)-based server
- **spring-server** Provides a [Spring Boot](https://spring.io/)-based server

### JavaFX Client Implementation

The JavaFX Client Implementation is located under in the **client** Maven module.

## Prerequisites

In order to build and run the examples, you must have the following installed:

* Maven 3.6.3+
* Java 11+
* NPM 6.14.4+
* Node.js 12.16.2+

## Build + Run Instructions

Please see the README for each server implementation for build and run instructions:

- [helidon-server](https://github.com/coherence-community/todo-list-example/tree/master/java/helidon-server)
- [micronaut-server](https://github.com/coherence-community/todo-list-example/tree/master/java/micronaut-server)
- [spring-server](https://github.com/coherence-community/todo-list-example/tree/master/java/spring-server)

## References

* [Coherence Community Edition](https://github.com/oracle/coherence)
* [Project Helidon](https://helidon.io/)
* [Coherence Community Home Page](https://coherence.community/)
* [Oracle JavaScript Extension Toolkit (JET)](https://www.oracle.com/webfolder/technetwork/jet/index.html)



