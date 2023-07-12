## Coherence To Do List Example Using `coherence-py-client`

### Overview

### Prerequisites

In order run the example, you must have the following installed:
* Python 3.11 or later

### Setup Instructions

1. Install the required libraries
   ```bash
   python3 -m pip install -r requirements.txt
   ```

### Running the Example

1. Start the Coherence server.  See the java [README](../java/README.md) for details.

2. Start the Quart server on port 7003:
    ```bash
    python3 app.py
    ```
    The start process should look something like:
   
    ```
   (venv) bash-3.2$ python3 app.py
   * Serving Quart app 'app'
   * Environment: production
   * Please use an ASGI server (e.g. Hypercorn) directly in production
   * Debug mode: False
   * Running on http://127.0.0.1:7003 (CTRL + C to quit)
   [2023-05-06 11:51:24 -0700] [63382] [INFO] Running on http://127.0.0.1:7003 (CTRL + C to quit)
   ```

4. Access the Web UI via [http://localhost:7003](http://localhost:7003)

![To Do List - React Client](../assets/react-client.png)