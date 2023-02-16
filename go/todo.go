/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

/*
To build the client executable use the following:

	go build -o ./client .
*/
package main

import (
	"bufio"
	"context"
	"errors"
	"fmt"
	"github.com/google/uuid"
	"github.com/oracle/coherence-go-client/coherence"
	"github.com/oracle/coherence-go-client/coherence/aggregators"
	"github.com/oracle/coherence-go-client/coherence/extractors"
	"github.com/oracle/coherence-go-client/coherence/filters"
	"github.com/oracle/coherence-go-client/coherence/processors"
	"log"
	"os"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"
)

var (
	ErrSupplyDescription = errors.New("you must supply a description")
	ctx                  = context.Background()
	namedMap             coherence.NamedMap[string, Task]
	todos                = make(map[int]Task, 0)
	mutex                sync.RWMutex
	completedExtractor   = extractors.Extract[bool]("completed")
	activeFilter         = filters.Equal[bool](completedExtractor, false)
	completedFilter      = filters.Equal[bool](completedExtractor, true)
	allFilter            = filters.Always()
	currentFilter        filters.Filter
)

const (
	addCommand      = "add"
	deleteCommand   = "delete"
	showCommand     = "show"
	completeCommand = "complete"
	updateCommand   = "update"
	clearCompleted  = "clear"
	todoFormat      = "%4s  %-10s  %-s\n"
)

// Task defines a task to be done.
type Task struct {
	//Class       string `json:"@class"` // require to be serialized as Java on the server
	CreatedAt   int64  `json:"createdAt"`
	Completed   bool   `json:"completed"`
	ID          string `json:"id"`
	Description string `json:"description"`
}

func main() {
	// create a new Session to the default gRPC port of 1408 using plain text
	session, err := coherence.NewSession(ctx, coherence.WithPlainText())
	if err != nil {
		log.Fatal(err)
	}
	defer session.Close()

	// create a new NamedMap of Task keyed by id
	namedMap, err = coherence.NewNamedMap[string, Task](session, "tasks")
	if err != nil {
		log.Fatal(err)
	}

	// set current filter to show all tasks
	currentFilter = allFilter

	// Create a listener and add to the cache
	listener := NewAllEventsListener[string, Task]()
	if err = namedMap.AddListener(ctx, listener.listener); err != nil {
		log.Fatal("unable to add listener", listener, err)
	}

	runREPL()
}

// runREPL display a REPL (read, eval, print, loop) for working with todo items
func runREPL() {
	var (
		err     error
		line    string
		scanner = bufio.NewScanner(os.Stdin)
	)

	// initial refresh from server
	refreshTodoMap()
	displayTodos()

	fmt.Println("type 'help' for help or 'quit' to exit.")
	for {
		fmt.Printf("Todo> ")
		scanner.Scan()
		line = scanner.Text()

		switch line {
		case "":
			continue
		case "help":
			showHelp()
			continue
		case "list":
			displayTodos()
			continue
		case "quit":
			return
		default:
			if err = processCommand(line); err != nil {
				fmt.Println(err)
				showHelp()
			}
		}
	}
}

// showHelp displays the help for the application.
func showHelp() {
	fmt.Printf(`
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
`)
}

// refreshTodoMap populates the todoMap from the current list of entries in the cache.
func refreshTodoMap() {
	tasks := make([]Task, 0)
	ch := namedMap.EntrySetFilter(ctx, currentFilter)
	for result := range ch {
		if result.Err != nil {
			log.Fatal(result.Err)
		}
		tasks = append(tasks, result.Value)
	}

	// sort the array by created by and assign an internal id
	sort.Slice(tasks, func(p, q int) bool {
		return tasks[p].CreatedAt < tasks[q].CreatedAt
	})
	mutex.Lock()
	defer mutex.Unlock()

	// re-create the task list
	todos = make(map[int]Task, 0)
	for i, task := range tasks {
		todos[i+1] = task
	}
}

// displayTodos displays the current list of todos.
func displayTodos() {
	var (
		active    *int64
		completed *int64
		err       error
		showing   string
	)

	fmt.Println()
	fmt.Printf(todoFormat, "ID", "Completed?", "Description")
	fmt.Printf(todoFormat, "----", "----------", "-----------------------------")
	for i, task := range todos {
		fmt.Printf(todoFormat, fmt.Sprintf("%v", i), fmt.Sprintf("%v", task.Completed), task.Description)
	}

	// note: the following could be retrieved from the todos map, but we are showing this
	// using coherence features for completeness
	active, err = coherence.AggregateFilter[string, Task](ctx, namedMap, activeFilter, aggregators.Count())
	if err != nil {
		log.Fatal(err)
	}

	completed, err = coherence.AggregateFilter[string, Task](ctx, namedMap, completedFilter, aggregators.Count())
	if err != nil {
		log.Fatal(err)
	}

	if currentFilter == activeFilter {
		showing = "active"
	} else if currentFilter == completedFilter {
		showing = "completed"
	} else {
		showing = "all"
	}

	fmt.Printf("\n%d active, %d completed, showing %s\n\n", *active, *completed, showing)
}

// processCommand processes a line.
func processCommand(line string) error {
	tokens := strings.Split(line, " ")
	if len(tokens) == 0 {
		return fmt.Errorf("invalid command '%s'", line)
	}

	if tokens[0] == addCommand {
		description := strings.Replace(line, addCommand+" ", "", 1)
		if description == "" {
			return ErrSupplyDescription
		}
		task := newTask(description)
		_, err := namedMap.Put(ctx, task.ID, task)
		return err
	}

	if tokens[0] == deleteCommand {
		id := strings.Replace(line, deleteCommand+" ", "", 1)
		parsedId, err := parseId(id)
		if err != nil {
			return err
		}

		_, err = namedMap.Remove(ctx, parsedId)
		return nil
	}

	if tokens[0] == completeCommand {
		id := strings.Replace(line, completeCommand+" ", "", 1)
		parsedId, err := parseId(id)
		if err != nil {
			return err
		}

		_, err = coherence.Invoke[string, Task, bool](ctx, namedMap, parsedId, processors.Update("completed", true))
		return err
	}

	if tokens[0] == clearCompleted {
		_ = coherence.InvokeAllFilter[string, Task, bool](ctx, namedMap, completedFilter, processors.ConditionalRemove(filters.Always()))
		return nil
	}

	if tokens[0] == updateCommand {
		command := strings.Replace(line, updateCommand+" ", "", 1)
		// we should
		parts := strings.Split(command, " ")
		if len(parts) < 2 {
			return fmt.Errorf("invalid update command %s", command)
		}

		updateId, err := parseId(parts[0])
		if err != nil {
			return err
		}

		newDescription := strings.Join(parts[1:], " ")

		_, err = coherence.Invoke[string, Task, bool](ctx, namedMap, updateId, processors.Update("description", newDescription))
		return err
	}

	if tokens[0] == showCommand {
		actualCommand := strings.Replace(line, showCommand+" ", "", 1)
		if actualCommand == "all" {
			currentFilter = allFilter
		} else if actualCommand == "completed" {
			currentFilter = completedFilter
		} else if actualCommand == "active" {
			currentFilter = activeFilter
		} else {
			return fmt.Errorf("invalid option of '%s' for show", actualCommand)
		}
		refreshTodoMap()
		displayTodos()
		return nil
	}

	return fmt.Errorf("invalid command: '%s'", line)
}

// parseId parses a specified id and returns the task Id that is associated with.
func parseId(id string) (string, error) {
	idNum, err := strconv.Atoi(id)
	if err != nil {
		return "", fmt.Errorf("id '%s' is invalid", id)
	}
	// retrieve the entry from the map to get the id
	task, ok := todos[idNum]
	if !ok {
		return "", fmt.Errorf("cannot find task for id '%s'", id)
	}

	return task.ID, nil
}

// newTask returns a new task with the specified description.
func newTask(description string) Task {
	return Task{
		//Class:       "Task",
		Description: description,
		ID:          uuid.New().String(),
		CreatedAt:   time.Now().UnixMilli(),
		Completed:   false,
	}
}

type AllEventsListener[K comparable, V any] struct {
	listener coherence.MapListener[K, V]
}

func NewAllEventsListener[K comparable, V any]() *AllEventsListener[K, V] {
	exampleListener := AllEventsListener[K, V]{
		listener: coherence.NewMapListener[K, V](),
	}

	// respond to any event and refresh and redisplay the last
	exampleListener.listener.OnAny(func(e coherence.MapEvent[K, V]) {
		refreshTodoMap()
		displayTodos()
	})

	return &exampleListener
}
