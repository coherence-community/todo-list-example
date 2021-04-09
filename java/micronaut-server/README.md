# Coherence To Do List Example Application for Micronaut

## Build Instructions

Run the following from the project root directory:

```bash
cd micronaut-server
mvn clean package
```

## Running the Example

```bash  
java -jar target/todo-list-micronaut-server-21.06-M1.jar
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