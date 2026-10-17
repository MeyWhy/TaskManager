
package com.taskmanager.taskmanager_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.taskmanager_api.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByCompleted(boolean completed, Pageable pageable);

    Page<Task> findByOwner_Username(
            String username, Pageable pageable);

    Page<Task> findByOwner_UsernameAndCompleted(
            String username, boolean completed, Pageable pageable);

    Optional<Task> findByIdAndOwner_Username(
            Long id, String username);
}
