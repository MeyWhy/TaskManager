
package com.taskmanager.taskmanager_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Task getTaskById(Long id) {
        return repository.findById(id);
    }

    public Task createTask(Task task) {
        task.setId(null);
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existing = repository.findById(id);
        return repository.update(existing, updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = repository.findById(id);
        repository.delete(task);
    }
}
