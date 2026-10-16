
package com.taskmanager.taskmanager_api.repository;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.TaskNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public TaskRepository() {
        tasks.add(new Task(
            nextId.getAndIncrement(),
            "Learn Spring Boot",
            "Build my first REST API",
            false
        ));

        tasks.add(new Task(
            nextId.getAndIncrement(),
            "Practice Java",
            "Understand classes and objects",
            true
        ));
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Task findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(nextId.getAndIncrement());
            tasks.add(task);
        }
        return task;
    }

    public Task update(Task existing, Task updated) {
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCompleted(updated.isCompleted());
        return existing;
    }

    public void delete(Task task) {
        tasks.remove(task);
    }
}
