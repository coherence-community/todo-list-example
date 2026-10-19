## Coherence To Do List Example Using `coherence-js-client`

### Overview

### Prerequisites

In order to build and run the examples, you must have the following installed:
* Node 18.15.x or later 
* NPM 9.x or later

### Build Instructions

1. Initialize `npm`
   ```bash
   npm install -s
   ```

### Running the Example

1. Start the server.  See the java [README](../java/README.md) for details.

2. Start the Express server on port 7002:
    ```bash
    npm run start
    ```
    or, to start the Express server on a different port:
    ```bash
    PORT=<port-number> npm run start
    ```

4. Access the Web UI via [http://localhost:7002](http://localhost:7002) (change 7002 to the specified custom port)

       ![To Do List - React Client](../assets/react-client.png)


Use a Coherence 26.10 server built from this repository with its application
security configuration. See [Java setup](../java/README.md#coherence-2610-and-hardened-mode).
After starting this example against a disposable empty server, run
`python3 bin/test-rest-api.py http://127.0.0.1:7002` from the repository root to
check CRUD, filtering, bulk removal and SSE. This check creates and removes task
data and requires an empty task map.
