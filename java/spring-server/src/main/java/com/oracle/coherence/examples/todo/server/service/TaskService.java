/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.server.service;

import com.oracle.coherence.examples.todo.server.model.Task;

import java.util.Collection;

/**
 * Task service that delegates to the underlying
 * {@link com.oracle.coherence.examples.todo.server.repository.TaskRepository}.
 * @author Gunnar Hillert
 */
public interface TaskService {
	Collection<Task> findAll(boolean completed);

	Task find(String id);

	void save(Task task);

	void removeById(String id);

	void deleteCompletedTasks();

	Collection<Task> deleteCompletedTasksAndReturnRemainingTasks();

	Task update(String id, Task task);
}
