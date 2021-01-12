# Coherence To Do List Example Application

## Overview

This example is a simple task management application which allows users to add, complete,
filter and delete tasks within a list.

The example currently provides two clients to work with tasks:

1. React front-end integrating with a Helidon Microprofile back-end integrated 
   with Coherence using the `coherence-cdi` and `coherence-mp` modules.

1. GraphQL end-point with optional GraphiQL UI using Helidon Microprofile GraphQL.

1. JavaFX client connecting as a Coherence Java client 

1. Oracle JET front-end (Optional)

Any number of the clients can be run and will receive all events from other clients as
tasks are created, updated, completed or removed. This is achieved using Server Sent Events 
(SSE) for React client and Coherence Live Events for the JavaFX client.

## Prerequisites

In order to build and run the examples, you must have the following installed:

* Maven 3.6.3+
* Java 11+
* NPM 6.14.4+
* Node.js 12.16.2+

## Build Instructions (React)

1. Initialize `npm`

    ```bash
    cd server/src/main/web/react
    npm install   
    ```           

1. Build the Project

    Run the following from the project root directory

    ```bash
    mvn clean install
    ```       

## Running the Example

1. Run the server

    ```bash  
    cd server
    mvn exec:exec
    ```            
   
1. Access the Web UI
  
   Access via http://localhost:7001/
   
   ![To Do List - React Client](../assets/react-client.png)
   
1. Display GraphQL Generated Schema

   ```bash
   curl http://localhost:7001/graphql/schema.graphql
   
   type Mutation {
      "Create a task with the given description"
      createTask(description: String): Task
      "Remove all completed tasks and return the number of tasks removed"
      deleteCompletedTasks: Int
      "Delete a task and return the deleted task details"
      deleteTask(id: String): Task
      "Update a task"
      updateTask(completed: Boolean, description: String, id: String): Task
    }
   
   type Query {
      "Return a given task"
      findTask(id: String): Task
      "Query tasks and optionally specify only completed"
      tasks(completed: Boolean): [Task]
    }
   
   type Task {
      completed: Boolean!
      createdAt: BigInteger!
      "yyyy-MM-dd'T'HH:mm:ss"
      createdAtDate: DateTime
      description: String
      id: String
    }
    
    "Custom: Built-in java.math.BigInteger"
    scalar BigInteger
    
    "Custom: An RFC-3339 compliant DateTime Scalar"
    scalar DateTime
    
    "Custom: An RFC-3339 compliant DateTime Scalar"
    scalar FormattedDateTime
    ``` 
   
1. Create a Task via GraphQL API

    ```bash
    curl -X POST http://127.0.0.1:7001/graphql -d '{"query":"mutation createTask { createTask(description: \"Task Description 1\") { id description createdAt completed }}"}'    

    Response is a newly created task:

    {"data":{"createTask":{"id":"0d4a8d","description":"Task Description 1","createdAt":1605501774877,"completed":false}} 
    ```   
      
1. Access GraphiQL UI

   The [GraphiQL UI](https://github.com/graphql/graphiql), which provides a UI to execute GraphQL commands, is not included by default in Helidon's Microprofile GraphQL 
   implementation. You can follow the guide below to incorporate the UI into this example:

    1. Copy the contents in the sample index.html file from [here](https://github.com/graphql/graphiql/blob/main/packages/graphiql/README.md)
       into the file at `java/server/src/main/resources/web/graphiql.html`

    1. Change the URL in the line `fetch('https://my/graphql', {` to `http://127.0.0.1:7001/graphql`

    1. Build an run the the project using the instructions above.

    1. Access the GraphiQL UI at `http://127.0.0.1/graphiql.html`

    ![To Do List - GraphiQL UI](../assets/graphiql-ui.png)

    1. Paste the following commands into the left pane and use the `Play` button to execute queries and mutations.

    ```graphql
    # Fragment to allow shorcut to display all fields for a task
    fragment task on Task {
      id
      description
      createdAt
      completed
    }

    # Create a task
    mutation createTask {
      createTask(description: "Task Description 1") {
        ...task
      }
    }

    # Create a task with empty description - will return error message
    # Normally unchecked exceptions will not be displayed but
    # We have overridden this in the microprofile-config.properties
    mutation createTaskWithoutDescription {
      createTask {
        ...task
      }
    }

    # Find all the tasks
    query findAllTasks {
      tasks {
        ...task
      }
    }

    # Find a task
    query findTask {
      findTask(id: "e07a00") {
        ...task
      }
    }

    # Find completed Tasks
    query findCompletedTasks {
      tasks(completed: true) {
        ...task
      }
    }

    # Find outstanding Tasks
    query findOutstandingTasks {
      tasks(completed: false) {
        ...task
      }
    }

    mutation updateTask {
      updateTask(id: "ad4b32" description:"New Description 2") {
        ...task
      }
    }

    mutation completeTask {
      updateTask(id: "b30c3d" completed:true) {
        ...task
      }
    }

    # Delete a task
    mutation deleteTask {
      deleteTask(id: "ad4b32") {
        ...task
      }
    }

    # Delete completed
    mutation deleteCompleted {
      deleteCompletedTasks
    }
    ```

1. Run the JavaFX Client

    ```bash  
    cd client
    mvn javafx:run
    ```  
        
    ![To Do List - JavaFX Client](../assets/javafx-client.png)

## Build and Run the Oracle JET UI (Optional)

If you wish to run the Oracle JET UI, please carry out the following:

1. Install the JET CLI

    ```bash
    npm install -g @oracle/ojet-cli
    ```   
   
1. Build JET UI
   
    ```bash
    cd server/src/main/web/jet
    npm install
    ojet build
    ```
            
1. Re-Build the Project

    Run the following from the project root directory:

    ```bash
    mvn clean install
    ```          

1. Access the Web UI

   Run the server as mentioned above and access the UI via http://localhost:7001/jet/   
   
   ![To Do List - Oracle JET Client](../assets/jet-client.png)
    
## References

* [Coherence Community Edition](https://github.com/oracle/coherence)
* [Project Helidon](https://helidon.io/)
* [Coherence Community Home Page](https://coherence.community/)
* [Oracle JavaScript Extension Toolkit (JET)](https://www.oracle.com/webfolder/technetwork/jet/index.html)



