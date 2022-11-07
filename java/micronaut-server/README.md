# Coherence To Do List Example Application for Micronaut

## Build Instructions

### Maven

```bash
mvn clean package
```

### Gradle

```bash
./gradlew clean build
```

## Running the Example

### Maven

```bash  
java -jar target/todo-list-micronaut-server-21.06.2.jar
```

## Building a Docker Image

### Maven

```bash
mvn -P docker clean install
```

### Running the Docker Container

```bash
docker run -d -p 1408:1408 -p 3000:3000 -p 9612:9612 ghcr.io/coherence-community/todo-list-micronaut-server:latest
```

NOTE: `1408` is the default gRPC port, `3000` is the HTTP port, and `9612` is the metrics port.

### Access the Web UI

Access via http://localhost:3000/

![To Do List - React Client](../../assets/react-client.png)

### Run the JavaFX Client

```bash  
cd ../coherence-client
mvn javafx:run
```

![To Do List - JavaFX Client](../../assets/javafx-client.png)

### Query the GraphQL Endpoint

The GraphQL Endpoint is available at: `http://localhost:3000/graphql`. Use one of the following tools to interact wih it:

- [GraphQL](https://github.com/graphql/graphiql)
- [Insomnia](https://insomnia.rest/download)

For instance, retrieve a collection of tasks using the following query:

```graphql
query {
  tasks(completed: false) {
    id
    description
    completed
    createdAt
    createdAtDate
  }
}
```

For more information on GraphQL and using it with the To Do List example, [please see here](../graphql.md).