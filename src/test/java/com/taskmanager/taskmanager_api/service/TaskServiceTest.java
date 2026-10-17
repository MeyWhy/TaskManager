
package com.taskmanager.taskmanager_api.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.exception.TaskNotFoundException;
import com.taskmanager.taskmanager_api.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task(
                1L,
                "Learn JUnit",
                "Write automated tests",
                false
        );
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        when(repository.findAll()).thenReturn(List.of(sampleTask));

        List<Task> result = service.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Learn JUnit", result.get(0).getTitle());
        verify(repository).findAll();
    }

    @Test
    void getTaskByIdShouldReturnTaskWhenItExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task result = service.getTaskById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Learn JUnit", result.getTitle());
        verify(repository).findById(1L);
    }

    @Test
    void getTaskByIdShouldThrowWhenTaskDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.getTaskById(99L)
        );

        verify(repository).findById(99L);
    }

    @Test
    void createTaskShouldSaveTaskAndClearItsId() {
        Task newTask = new Task(
                50L,
                "New task",
                "Testing creation",
                false
        );

        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(2L);
            return task;
        });

        Task result = service.createTask(newTask);

        assertEquals(2L, result.getId());
        assertEquals("New task", result.getTitle());
        verify(repository).save(newTask);

        // The service should clear the ID before saving a new task.
        assertEquals(2L, result.getId());
    }

    @Test
    void updateTaskShouldUpdateExistingTask() {
        Task updatedTask = new Task(
                null,
                "Updated title",
                "Updated description",
                true
        );

        when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(repository.save(any(Task.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        Task result = service.updateTask(1L, updatedTask);

        assertEquals(1L, result.getId());
        assertEquals("Updated title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertTrue(result.isCompleted());

        verify(repository).findById(1L);
        verify(repository).save(sampleTask);
    }

    @Test
    void deleteTaskShouldDeleteExistingTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));

        service.deleteTask(1L);

        verify(repository).findById(1L);
        verify(repository).delete(sampleTask);
    }

    @Test
    void deleteTaskShouldThrowWhenTaskDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.deleteTask(99L)
        );

        verify(repository, never()).delete(any(Task.class));
    }
}
