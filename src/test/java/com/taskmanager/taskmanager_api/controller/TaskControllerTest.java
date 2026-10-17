
package com.taskmanager.taskmanager_api.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.exception.GlobalExceptionHandler;
import com.taskmanager.taskmanager_api.exception.TaskNotFoundException;
import com.taskmanager.taskmanager_api.service.TaskService;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

@Test
void getAllTasksShouldReturn200AndJson() throws Exception {
    Task task = new Task(
        1L,
        "Learn Spring",
        "Practice REST APIs",
        false
    );

    Pageable pageable = PageRequest.of(
        0, 10, Sort.by(Sort.Direction.DESC, "id")
    );

    when(service.getTasks(any(Pageable.class), any()))
        .thenReturn(new PageImpl<>(List.of(task), pageable, 1));

    mockMvc.perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Learn Spring"))
        .andExpect(jsonPath("$.content[0].completed").value(false))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.number").value(0));
}

    @Test
    void createTaskShouldReturn201AndCreatedTask() throws Exception {
        when(service.createTask(any(Task.class))).thenReturn(
            new Task(1L, "Learn Spring", "Practice REST APIs", false)
        );

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Learn Spring",
                        "description": "Practice REST APIs",
                        "completed": false
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Learn Spring"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createTaskShouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "   ",
                        "description": "A task without a valid title",
                        "completed": false
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getTaskByIdShouldReturn404WhenTaskDoesNotExist() throws Exception {
        when(service.getTaskById(99L))
            .thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
