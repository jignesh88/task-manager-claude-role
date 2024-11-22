package com.grabduck.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.dto.PagedResponseDto;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import com.grabduck.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private Task testTask;
    private UUID testTaskId;

    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();
        testTask = new Task(
                testTaskId,
                "Test Task",
                "Test Description",
                LocalDateTime.now().plusDays(1),
                TaskStatus.NOT_STARTED,
                TaskPriority.MEDIUM,
                List.of("test", "unit-test")
        );
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Test Task")))
                .andExpect(jsonPath("$.tags", hasSize(2)));
    }

    @Test
    void createTask_InvalidTask_ReturnsBadRequest() throws Exception {
        when(taskService.createTask(any(Task.class)))
                .thenThrow(new InvalidTaskException("Invalid task data"));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid task data")));
    }

    @Test
    void getTask_ExistingTask_ReturnsTask() throws Exception {
        when(taskService.getTaskById(testTaskId)).thenReturn(testTask);

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Test Task")));
    }

    @Test
    void getTask_NonExistingTask_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(testTaskId))
                .thenThrow(new TaskNotFoundException(testTaskId));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found with id: " + testTaskId)));
    }

    @Test
    void updateTask_ValidTask_ReturnsUpdatedTask() throws Exception {
        Task updatedTask = new Task(
                testTaskId,
                "Updated Task",
                "Updated Description",
                testTask.dueDate(),
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                List.of("updated", "test")
        );

        when(taskService.updateTask(eq(testTaskId), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/{taskId}", testTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void deleteTask_ExistingTask_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_NonExistingTask_ReturnsNotFound() throws Exception {
        doThrow(new TaskNotFoundException(testTaskId))
                .when(taskService).deleteTask(testTaskId);

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks_ValidParameters_ReturnsPagedResponse() throws Exception {
        Page<Task> page = new Page<>(
                List.of(testTask),
                1L,
                1,
                10,
                0
        );

        when(taskService.getTasks(any(), any(), any(), any(), eq(0), eq(10), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Task")));
    }

    @Test
    void getTasks_InvalidPageSize_ReturnsBadRequest() throws Exception {
        when(taskService.getTasks(any(), any(), any(), any(), eq(0), eq(200), any()))
                .thenThrow(new InvalidTaskException("Page size must be between 1 and 100"));

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "200"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Page size must be between 1 and 100")));
    }
}
