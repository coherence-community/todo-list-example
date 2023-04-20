# Coherence To Do List Example Using coherence-go-client

### Overview

### Prerequisites

In order to build and run the Go To Do List example, you must have the following installed:
* Go 1.19 or above - see [https://go.dev/dl/](https://go.dev/dl/)

### Build Instructions

1. Clone this repository

   ```bash
   git clone https://github.com/coherence-community/todo-list-example.git
   cd todo-list-example/go
   ```

2. Install the `coherence-go-client`

   ```bash
   go get github.com/coherence/coherence-go-client@latest
   ```

### Running the Example

1. Start the server.  See the java [README](../java/README.md) for details.

2. Run `todo.go` CLI

   ```bash
   cd todo-list-example/go
   go run todo.go
   ```
   
   You can also create a binary for your platform.

   ```bash
   go build -o ./todo .
   ./todo
   ```

   > Note: You should open one of the other UI's open to see the To Do list changes. You can also open 
   > a second `todo.go` application if you want. 

3. Use the CLI

    Once the `Todo>` prompt is displayed you can type `help` to display the list of commands.
  
   ```bash
   2023/04/20 13:14:35 session: 392fbc7f-2a7b-4689-8a31-3b9015e4397d connected to address localhost:1408

   ID    Completed?  Description  
   ----  ----------  -----------------------------

   0 active, 0 completed, showing all

   type 'help' for help or 'quit' to exit.
   Todo> help

   Commands:
   - help:     display help
   - quit:     exit the Todo application
   - add:      add a new task
   - delete:   delete a task
   - update:   update the description of a task
   - complete: complete a task
   - clear:    clear completed
   - show:     show all, active or completed tasks
   - list:     show the list of tasks
   ```
   
   * Insert a "to do" by typing `add Mow the Lawn` and press enter. You should see this new task displayed on your CLI and any other UI's.
   * Add a second "to do" `add Get milk`.
   * Complete the first "to do" using `complete 1`. You will see something similar to the following:
     ```bash
     ID     Completed?  Description
     ----  ----------  -----------------------------
     1     true        Mow the lawn
     2     false       Get milk
   
     0 active, 1 completed, showing all
     ```
   * From a different UI or `todo` CLI type `clear` which will clear the completed tasks.
   * Update a "to do" description using `update 1 Get more milk`, you will see this updated.


   You can experiment with the other commands. Type `quit` to exit the CLI.

## More Information

* [Coherence Go Client on GitHub](https://github.com/oracle/coherence-go-client)
* [Coherence Community](https://coherence.community/)