/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.controller;

import java.util.Collection;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.service.SseService;
import com.oracle.coherence.examples.todo.server.service.TaskService;
import com.oracle.coherence.spring.annotation.event.*;
import com.oracle.coherence.spring.event.CoherenceEventListener;
import com.tangosol.util.MapEvent;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST API for To Do list management.
 *
 * @author Gunnar Hillert
 */
@RestController
@RequestMapping("/api/tasks")
public class ToDoController
    {
    private TaskService taskService;

    private SseService sseService;

    public ToDoController(TaskService taskService, SseService sseService)
        {
        this.taskService = taskService;
        this.sseService = sseService;
        }

    @CoherenceEventListener
    public void broadCastEvents(@MapName("tasks") @Inserted @Updated @Deleted MapEvent<String, Task> event)
        {
        final String eventName;
        final Task taskToSend;

        if (event.isInsert())
            {
            eventName = "insert";
            taskToSend = event.getNewValue();
            }
        else if (event.isUpdate())
            {
            eventName = "update";
            taskToSend = event.getNewValue();
            }
        else if (event.isDelete())
            {
            eventName = "delete";
            taskToSend = event.getOldValue();
            }
        else
            {
            throw new IllegalStateException("Unsupported event " + event);
            }

        this.sseService.sendEventToClients(eventName, taskToSend);
        }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Task> getTasks(@RequestParam(defaultValue = "false") boolean completed)
        {
        return taskService.findAll(completed);
        }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createTask(@RequestBody Task task)
        {
        taskService.save(new Task(task.getDescription()));
        }

    @DeleteMapping("{id}")
    public void deleteTask(@PathVariable("id") String id)
        {
        taskService.removeById(id);
        }

    @DeleteMapping
    public void deleteCompletedTasks()
        {
        taskService.deleteCompletedTasks();
        }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task updateTask(@PathVariable("id") String id, @RequestBody Task task)
        {
        return taskService.update(id, task);
        }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSseMvc()
        {
        return sseService.registerClient();
        }
    }
