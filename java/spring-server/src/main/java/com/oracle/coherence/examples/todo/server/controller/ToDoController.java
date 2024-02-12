/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.controller;

import java.util.Collection;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.service.SseService;
import com.oracle.coherence.examples.todo.server.service.TaskService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST API for To Do list management.
 *
 * @author Gunnar Hillert
 */
@RestController
@RequestMapping(path="/api/tasks",
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ToDoController
    {
    private final TaskService taskService;

    private final SseService sseService;

    public ToDoController(TaskService taskService, SseService sseService)
        {
        this.taskService = taskService;
        this.sseService = sseService;
        }

    @GetMapping
    public Collection<Task> getTasks(@RequestParam(defaultValue = "false") boolean completed)
        {
        return taskService.findAll(completed);
        }

    @PostMapping
    public void createTask(@RequestBody Task task)
        {
        taskService.save(new Task(task.getDescription()));
        }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id)
        {
        taskService.removeById(id);
        }

    @DeleteMapping
    public void deleteCompletedTasks()
        {
        taskService.deleteCompletedTasks();
        }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task task)
        {
        return taskService.update(id, task);
        }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registerSseClient()
        {
        return sseService.registerClient();
        }
    }
