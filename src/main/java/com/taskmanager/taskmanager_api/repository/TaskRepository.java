package com.taskmanager.taskmanager_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.taskmanager_api.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
}