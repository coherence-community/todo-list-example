package com.oracle.coherence.examples.todo.server.service;

import com.oracle.coherence.examples.todo.server.model.Task;

import java.util.Collection;

public interface TaskService {
	Collection<Task> findAll(boolean completed);

	Task find(String id);

	void save(Task task);

	void removeById(String id);

	void deleteCompletedTasks();

	Collection<Task> deleteCompletedTasksAndReturnRemainingTasks();

	Task update(String id, Task task);
}
