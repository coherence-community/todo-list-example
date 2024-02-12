/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.service;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.repository.SpringDataTaskRepository;
import com.oracle.coherence.examples.todo.server.repository.TaskRepository;
import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.PostConstruct;
import java.util.Collection;

/**
 * A {@link TaskService} to interact with the underlying {@link SpringDataTaskRepository} for working with {@link Task tasks}.
 * @author Gunnar Hillert
 */
@Service
@Profile("!coherence")
public class SpringDataTaskService implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDataTaskService.class);

    private static final String TASK_NOT_FOUND_MESSAGE = "Unable to find task with id '%s'.";

    private final SpringDataTaskRepository taskRepository;

    private final SseService sseService;

    public SpringDataTaskService(SpringDataTaskRepository taskRepository, SseService sseService) {
        this.taskRepository = taskRepository;
        this.sseService = sseService;
    }

    @Override
    public Collection<Task> findAll(boolean completed)
        {
        final Filter<Task> filter = !completed
                ? Filters.always()
                : Filters.equal(Task::getCompleted, true);
        return taskRepository.getAllOrderedBy(filter, Task::getCreatedAt);
        }

    @Override
    public Task find(String id)
        {
        Assert.hasText(id, "The Task Id must not be null or empty.");
        final Task task = taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, id)));

        return task;
        }

    @Override
    public void save(Task task)
        {
        Assert.notNull(task, "The task must not be null.");
        taskRepository.save(task);
        }

    @Override
    public void removeById(String id)
        {
        Assert.hasText(id, "The Task Id must not be null or empty.");
        taskRepository.deleteById(id);
        }

    @Override
    public void deleteCompletedTasks()
        {
        taskRepository.deleteAll(Filters.equal(Task::getCompleted, true), false);
        }

    @Override
    public Collection<Task> deleteCompletedTasksAndReturnRemainingTasks()
        {
        this.deleteCompletedTasks();
        return this.findAll(false);
        }

    @Override
    public Task update(String id, Task task)
        {
        Assert.hasText(id, "The Task Id must not be null or empty.");
        Assert.notNull(task, "The Task must not be null.");

        final String description = task.getDescription();
        final Boolean completed = task.getCompleted();

        try
            {
            return this.taskRepository.update(id, tsk ->
                {
                if (description != null)
                    {
                    tsk.setDescription(description);
                    }
                if (completed != null)
                    {
                    tsk.setCompleted(completed);
                    }
                return tsk;
                });
            }
        catch (Exception ex)
            {
            throw new TaskNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, id));
            }
        };

    @PostConstruct
    public void init()
        {
        LOGGER.info("Using SpringDataTaskService.");
        final TaskRepository.Listener<Task> listener = this.taskRepository.listener()
                .onInsert(task -> {
                    sseService.sendEventToClients("insert", task);
                })
                .onUpdate(task -> {
                    sseService.sendEventToClients("update", task);
                })
                .onRemove(task -> {
                    sseService.sendEventToClients("delete", task);
                }).build();
        this.taskRepository.addListener(listener);
        }
}
