/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.service;

import com.oracle.coherence.examples.todo.server.model.Task;
import com.oracle.coherence.examples.todo.server.repository.TaskRepository;
import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;

/**
 * A service to interact with the underlying {@link TaskRepository} for working with {@link Task tasks}.
 * @author Gunnar Hillert
 */
@Service
public class TaskService
    {

    private static final String TASK_NOT_FOUND_MESSAGE = "Unable to find task with id '%s'.";

    @Autowired
    private TaskRepository taskRepository;

    public Collection<Task> findAll(boolean completed)
        {
        final Filter<Task> filter = !completed
                ? Filters.always()
                : Filters.equal(Task::getCompleted, true);
        return taskRepository.findAll(filter);
        }

    public Task find(String id)
        {
        final Task task = taskRepository.findById(id);

        if (task == null)
            {
            throw new TaskNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, id));
            }

        return task;
        }

    public void save(Task task)
        {
        taskRepository.save(task);
        }

    public void removeById(String id)
        {
        boolean wasRemoved = taskRepository.removeById(id);

        if (!wasRemoved)
            {
            throw new TaskNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, id));
            }
        }

    public void deleteCompletedTasks()
        {
        taskRepository.removeAll(Filters.equal(Task::getCompleted, true), false);
        }

    public Collection<Task> deleteCompletedTasksAndReturnRemainingTasks()
        {
        this.deleteCompletedTasks();
        return this.findAll(false);
        }

    public Task update(String id, Task task)
        {
        final String description = task.getDescription();
        final Boolean completed = task.getCompleted();

        try
            {
            return this.taskRepository.update(id, tsk ->
                {
                Objects.requireNonNull(tsk);

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
}
