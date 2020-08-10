# Coherence To Do List Sample Application

## Overview

## Table of Contents

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
   
1. Run the JavaFX Client

    ```bash  
    cd client
    mvn clean install
    mvn exec:exec
    ```            




