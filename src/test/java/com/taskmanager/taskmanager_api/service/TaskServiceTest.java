
package com.taskmanager.taskmanager_api.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.User;
import com.taskmanager.taskmanager_api.exception.TaskNotFoundException;
import com.taskmanager.taskmanager_api.repository.TaskRepository;
import com.taskmanager.taskmanager_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService service;

    private Task sampleTask;
    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User(1L, "alice", "alice@example.com", "hashed-password");

        sampleTask = new Task(
                1L,
                "Learn JUnit",
                "Write automated tests",
                false
        );
        sampleTask.setOwner(alice);
    }

    @Test
    void getTasksShouldReturnOnlyTasksForTheRequestedUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> expectedPage =
                new PageImpl<>(java.util.List.of(sampleTask), pageable, 1);

        when(taskRepository.findByOwner_Username("alice", pageable))
                .thenReturn(expectedPage);

        Page<Task> result = service.getTasks("alice", pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Learn JUnit", result.getContent().get(0).getTitle());

        verify(taskRepository).findByOwner_Username("alice", pageable);
    }

    @Test
    void getTasksShouldFilterByCompletionForTheRequestedUser() {
        Pageable pageable = PageRequest.of(0, 10);

        when(taskRepository.findByOwner_UsernameAndCompleted(
                "alice", false, pageable))
                .thenReturn(new PageImpl<>(java.util.List.of(sampleTask)));

        Page<Task> result = service.getTasks("alice", pageable, false);

        assertEquals(1, result.getTotalElements());
        assertTrue(!result.getContent().get(0).isCompleted());

        verify(taskRepository)
                .findByOwner_UsernameAndCompleted("alice", false, pageable);
    }

    @Test
    void getTaskByIdShouldReturnTaskWhenItBelongsToUser() {
        when(taskRepository.findByIdAndOwner_Username(1L, "alice"))
                .thenReturn(Optional.of(sampleTask));

        Task result = service.getTaskById(1L, "alice");

        assertEquals(1L, result.getId());
        assertEquals("Learn JUnit", result.getTitle());

        verify(taskRepository).findByIdAndOwner_Username(1L, "alice");
    }

    @Test
    void getTaskByIdShouldThrowWhenTaskDoesNotBelongToUser() {
        when(taskRepository.findByIdAndOwner_Username(1L, "bob"))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.getTaskById(1L, "bob")
        );

        verify(taskRepository).findByIdAndOwner_Username(1L, "bob");
    }

    @Test
    void getTaskByIdShouldThrowWhenTaskDoesNotExist() {
        when(taskRepository.findByIdAndOwner_Username(99L, "alice"))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.getTaskById(99L, "alice")
        );
    }

    @Test
    void createTaskShouldAssignOwnerAndSaveTask() {
        Task newTask = new Task(
                null,
                "New task",
                "Testing creation",
                false
        );

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(alice));

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(2L);
            return task;
        });

        Task result = service.createTask(newTask, "alice");

        assertEquals(2L, result.getId());
        assertEquals("New task", result.getTitle());
        assertNotNull(result.getOwner());
        assertEquals("alice", result.getOwner().getUsername());

        verify(userRepository).findByUsername("alice");
        verify(taskRepository).save(newTask);
    }

    @Test
    void createTaskShouldThrowWhenOwnerDoesNotExist() {
        Task newTask = new Task(
                null, "New task", "Testing creation", false
        );

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.createTask(newTask, "unknown")
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTaskShouldUpdateOnlyTheOwnersTask() {
        Task updatedTask = new Task(
                null,
                "Updated title",
                "Updated description",
                true
        );

        when(taskRepository.findByIdAndOwner_Username(1L, "alice"))
                .thenReturn(Optional.of(sampleTask));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = service.updateTask(1L, updatedTask, "alice");

        assertEquals(1L, result.getId());
        assertEquals("Updated title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertTrue(result.isCompleted());
        assertEquals("alice", result.getOwner().getUsername());

        verify(taskRepository).findByIdAndOwner_Username(1L, "alice");
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void updateTaskShouldNotModifyAnotherUsersTask() {
        Task updatedTask = new Task(
                null, "Hijacked title", "Unauthorized change", true
        );

        when(taskRepository.findByIdAndOwner_Username(1L, "bob"))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.updateTask(1L, updatedTask, "bob")
        );

        assertEquals("Learn JUnit", sampleTask.getTitle());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTaskShouldDeleteOwnersTask() {
        when(taskRepository.findByIdAndOwner_Username(1L, "alice"))
                .thenReturn(Optional.of(sampleTask));

        service.deleteTask(1L, "alice");

        verify(taskRepository).findByIdAndOwner_Username(1L, "alice");
        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void deleteTaskShouldNotDeleteAnotherUsersTask() {
        when(taskRepository.findByIdAndOwner_Username(1L, "bob"))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.deleteTask(1L, "bob")
        );

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
