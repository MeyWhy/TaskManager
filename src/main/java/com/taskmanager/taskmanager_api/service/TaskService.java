
package com.taskmanager.taskmanager_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.User;
import com.taskmanager.taskmanager_api.exception.TaskNotFoundException;
import com.taskmanager.taskmanager_api.repository.TaskRepository;
import com.taskmanager.taskmanager_api.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<Task> getTasks(
            String username, Pageable pageable, Boolean completed) {

        if (completed == null) {
            return taskRepository.findByOwner_Username(username, pageable);
        }

        return taskRepository.findByOwner_UsernameAndCompleted(
                username, completed, pageable);
    }

    public Task getTaskById(Long id, String username) {
        return taskRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    public Task createTask(Task task, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new org.springframework.security.core.userdetails
                                .UsernameNotFoundException("User not found"));

        task.setOwner(owner);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, String username) {
        Task existingTask = getTaskById(id, username);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    @Transactional
    public void deleteTask(Long id, String username) {
        Task task = getTaskById(id, username);
        taskRepository.delete(task);
    }
}
