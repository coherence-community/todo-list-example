# Coherence To Do List Server Example

## Build Instructions

The command below will build the server image.
The image will be created with two tags `todo-list-coherence-server:latest`
and `todo-list-coherence-server:${project.version}`

```bash
mvn clean package -Pdocker
```

## Run a Server Container

The command below will run the image in a container named `todo-server` 
and expose Coherence gRPC on `localhost:1408`.

```bash
docker run -d -p 1408:1408 --name todo-server ghcr.io/coherence-community/todo-list-coherence-server:latest
```

Running the `docker ps` command will display the status of the running image.

```
CONTAINER ID   IMAGE                               COMMAND                  CREATED          STATUS                            PORTS                              NAMES
0436f2dba284   todo-list-coherence-server:latest   "java -cp /app/class…"   3 seconds ago    Up 2 seconds (health: starting)   0.0.0.0:1408->1408/tcp, 9612/tcp   todo-server
 ```

Initially the `STATUS` colum will show `(health: starting)`. 
Once the Coherence health checks have a status of "ready", the container status will change to `(healthy)`

The Coherence client can now be run against the server image.

## Stop the Container

To stop and remove the image run the following command:

```bash
docker rm -f todo-server 
```
