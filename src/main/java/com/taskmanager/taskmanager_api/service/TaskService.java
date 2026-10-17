
package com.taskmanager.taskmanager_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.exception.TaskNotFoundException;
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

    public Page<Task> getTasks(Pageable pageable, Boolean completed) {
        if (completed != null) {
            return repository.findByCompleted(completed, pageable);
        }

        return repository.findAll(pageable);
    }

    public Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(Task task) {
        task.setId(null);
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return repository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        repository.delete(task);
    }
}
