# Coherence To Do List Example Application for Spring Boot

## Build Instructions

Run the following from the project root directory:

```bash
cd spring-server
mvn clean package
```

## Running the Example

```bash
java -jar target/todo-list-spring-server-21.06-M2.jar
```

The Coherence Spring implementation comes with 2
[TaskService](https://github.com/coherence-community/todo-list-example/blob/master/java/spring-server/src/main/java/com/oracle/coherence/examples/todo/server/service/TaskService.java) implementations:

- [SpringDataTaskService](https://github.com/coherence-community/todo-list-example/blob/master/java/spring-server/src/main/java/com/oracle/coherence/examples/todo/server/service/SpringDataTaskService.java)
- [CoherenceTaskService](https://github.com/coherence-community/todo-list-example/blob/master/java/spring-server/src/main/java/com/oracle/coherence/examples/todo/server/service/CoherenceTaskService.java)

By default, the Spring Data-based implementation is used, but you can activate the native
Coherence implementation by enabling the `Coherence` Spring Boot profile:

```bash
java -jar target/todo-list-spring-server-21.06-M2.jar --spring.profiles.active=coherence
```

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

- [GraphiQL](https://github.com/graphql/graphiql)
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

For more information on GraphQL and using it with the To Do List example, [please see the here](../graphql.md).
