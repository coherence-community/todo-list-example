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
java -jar target/todo-list-micronaut-server-1.0.0.jar
```

### Gradle

```bash
./gradlew run
```

## Building a Docker Image

### Maven

```bash
mvn -P docker install
```

### Gradle

```bash
./gradlew jibDockerBuild
```

### Running the Docker Container

```bash
docker run -d -p 1408:1408 -p 3000:3000 ghcr.io/coherence-community/todo-list-micronaut-server:21.06.1
```

NOTE: `1408` is the default gRPC port and `3000` is the HTTP port.

### Access the Web UI

Access via http://localhost:3000/

![To Do List - React Client](../../assets/react-client.png)

### Run the JavaFX Client

```bash  
cd ../client
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