# Coherence To Do List Example Application

## Overview

This example is a simple task management application which allows users to add, complete,
filter and delete tasks within a list.

The example currently provides two clients to work with tasks:

1. React front-end integrating with a Helidon Microprofile back-end integrated 
   with Coherence using the `coherence-cdi` and `coherence-mp` modules.

1. JavaFX client connecting as a Coherence Java client 

Any number of the clients can be run and will receive all events from other clients as
tasks are created, updated, completed or removed. This is achieved using Server Sent Events 
(SSE) for React client and Coherence Live Events for the JavaFX client.

## Prerequisites

In order to build and run the examples, you must have the following installed:

* Maven 3.5.4+
* JDK 11+
* NPM 6.14.4+

## Build Instructions

1. Initialize `npm`

    ```bash
    cd server/src/main/web
    npm install
    ``` 
   
1. Build the Project

    > Note: Run the following from the project root directory

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
   
   Access via: http://localhost:7001/
   
   ![To Do List ](assets/react-client.png)
   
1. Access the Web UI on iOS
   
    ![To Do List ](assets/ios-safari.png)  
   
1. Run the JavaFX Client

    ```bash  
    cd client
    mvn clean install
    mvn exec:exec
    ```  
             
    ![To Do List ](assets/javafx-client.png)

## References

* [Coherence Community Edition](https://github.com/oracle/coherence)
* [Project Helidon](https://helidon.io/)
* [Coherence Ccommunity](https://coherence.community/)



