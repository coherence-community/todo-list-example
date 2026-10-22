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

Four server implementation are provided by the following Maven modules:

- **coherence-server** Provides a simple Coherence server and Docker image
- **helidon-server** Provides a [Helidon](https://helidon.io/)-powered server
- **micronaut-server** Provides a [Micronaut](https://micronaut.io/)-based server
- **spring-server** Provides a [Spring Boot](https://spring.io/)-based server

### JavaFX Client Implementation

The JavaFX clients are in **coherence-client** (Coherence gRPC) and
**helidon-client** (the Helidon application gRPC API).

## Prerequisites

In order to build and run the examples, you must have the following installed:

* Maven 3.6.3+
* Java 21+
* NPM 8.x+
* Node.js 14+
* Docker to build the Coherence server image

## Build + Run Instructions

Please see the README for each server implementation for build and run instructions:

- [helidon-server](https://github.com/coherence-community/todo-list-example/tree/master/java/helidon-server)
- [micronaut-server](https://github.com/coherence-community/todo-list-example/tree/master/java/micronaut-server)
- [spring-server](https://github.com/coherence-community/todo-list-example/tree/master/java/spring-server)

## REST and SSE smoke checks

Against a disposable empty server, run `python3 bin/test-rest-api.py
http://127.0.0.1:7001 --graphql` from the repository root. The packaged-server
check in `bin/test-fat-jar.sh` also uses this validator. Before checking the first
insert, it repeatedly updates a separate probe task until an update arrives over
SSE, with a bounded timeout. This confirms listener readiness without relying on
a startup delay or the server's `begin` event. The probe is removed, and insert,
update and delete events are then checked for the test task's own ID.

Run the validator's loopback HTTP regression tests without Java or Coherence:
`python3 -m unittest discover -s bin -p '*_test.py' -v`. CI runs these before
building the examples.

## References

* [Coherence Community Edition](https://github.com/oracle/coherence)
* [Project Helidon](https://helidon.io/)
* [Coherence Community Home Page](https://coherence.community/)
* [Oracle JavaScript Extension Toolkit (JET)](https://www.oracle.com/webfolder/technetwork/jet/index.html)




## Coherence 26.10 and hardened mode

The Maven modules target `26.10-SNAPSHOT`. Build all modules from the repository
root with JDK 21:

```bash
mvn -B -f java/pom.xml -s java/.mvn/settings.xml clean install -Pdocker
```

The Java Coherence client integration test uses the matching locally built server
image. Docker is required for that test; `-Dskip.containers=true` skips it when
building without Docker. The Helidon client has a separate headless gRPC check:
start Helidon, then run `mvn -f java/helidon-client/pom.xml -s java/.mvn/settings.xml
-Dtodo.helidon.integration=true verify` from the repository root.

Each application supplies `META-INF/coherence/security-config.xml` for its Task
class and, where applicable, its gRPC request classes. These entries permit data
serialization; Coherence's own metadata permits the built-in operations used by
the examples. Micronaut's shaded jar merges these resources using the Coherence
security resource transformer. Leave `coherence.security.mode` unset to use the
hardened default.

Coherence metrics now require HTTP Basic authentication by default, including in
development mode. The Kubernetes examples that enable Coherence metrics
explicitly set `-Dcoherence.metrics.http.auth=none` in `spec.jvm.args` and enable
their ServiceMonitors so this demo can be scraped without credentials. This
override makes metrics readable by anyone who can reach the endpoint; it leaves
serialization hardening enabled. Prometheus and the Prometheus Operator must
already be installed and configured to discover these ServiceMonitors.

For authenticated metrics, remove the `none` override, configure the
`CoherenceREST` JAAS login context, and supply matching scraper credentials.
The fixed credentials in `test-common` are test fixtures and are not packaged
with the applications. Framework HTTP monitoring is unchanged.

The Helidon Kubernetes and MicroK8s examples monitor both Helidon HTTP metrics
on port 7001 and separate Coherence metrics on port 9612, each at `/metrics`.
See [Helidon monitoring](helidon-server/README.md#monitoring) for the generated
ServiceMonitors, discovery prerequisites, and demo authentication policy.

The examples use plaintext local HTTP/gRPC connections. The migration validates
local client interoperability; configure TLS and authentication for deployments
that require them. Kubernetes manifests reference the `26.10-SNAPSHOT` image tag;
build and make those images available to your cluster before applying them.

When 26.10 is released, first qualify it using `-Dcoherence.version=26.10`, which
overrides the Coherence dependencies and build plugins in every Java module.
Then replace the example project/parent versions and deployment image tags with
the actual released version and repeat the qualification. The non-Java clients
have independent version numbers and do not use a `26.10` client version.
