# todo-list-example
Coherence To Do List Sample Application

## Overview

## Table of Contents

## Prerequisites

### NPM

## Build Instructions

```bash
mvn clean install
```

```bash
cd server/src/main/web
yarn install
yarn build       
mkdir -p ../resources/web
cp -R build/ ../resources/web/
```       

## Running the Example

1. Run the server

   ```bash  
   cd server
   mvn exec:exec
   ```            
   
   Access via: http://localhost:7001/
   
1. Run the JavaFX Client

   ```bash  
   cd client
   mvn exec:exec
   ```            




