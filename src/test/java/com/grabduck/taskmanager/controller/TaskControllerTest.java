package com.grabduck.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabduck.taskmanager.config.TestSecurityConfig;
import com.grabduck.taskmanager.config.TestJwtConfig;
import com.grabduck.taskmanager.domain.*;
import com.grabduck.taskmanager.dto.CreateTaskRequest;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import com.grabduck.taskmanager.repository.UserRepository;
import com.grabduck.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import({TestSecurityConfig.class, TestJwtConfig.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    private Task testTask;
    private UUID testTaskId;
    private UUID testOwnerId;
    private UserDetails testUser;
    private com.grabduck.taskmanager.domain.User testDomainUser;

    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();
        testOwnerId = UUID.randomUUID();
        testTask = new Task(
                testTaskId,
                "Test Task",
                "Test Description",
                LocalDateTime.now().plusDays(1),
                TaskStatus.NOT_STARTED,
                TaskPriority.MEDIUM,
                Set.of("test", "important"),
                testOwnerId
        );

        testUser = User.withUsername("user")
                .password("password")
                .roles("USER")
                .build();

        testDomainUser = new com.grabduck.taskmanager.domain.User(
                testOwnerId,
                "user",
                "password",
                "user@example.com",
                Set.of(UserRole.USER)
        );

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(testDomainUser));

        // Mock default behavior for findTasks
        Page<Task> defaultPage = new Page<>(
                List.of(testTask),
                1L,
                1,
                10,
                0
        );
        when(taskService.findTasks(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt(),
                any(SortOption.class)
        )).thenReturn(defaultPage);
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void createTask_ValidTask_ReturnsCreatedTask() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task",
                "Test Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.MEDIUM,
                Set.of("test", "important")
        );

        when(taskService.createTask(any(CreateTaskRequest.class), any(String.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.status", is("NOT_STARTED")))
                .andExpect(jsonPath("$.priority", is("MEDIUM")))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.ownerId", is(testOwnerId.toString())));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void createTask_InvalidTask_ReturnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "",
                "Test Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.MEDIUM,
                Set.of("test", "important")
        );

        doThrow(new InvalidTaskException("Task name cannot be empty"))
                .when(taskService).createTask(any(CreateTaskRequest.class), any(String.class));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Task name cannot be empty")));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void getTask_ExistingTask_ReturnsTask() throws Exception {
        when(taskService.getTaskById(testTaskId)).thenReturn(testTask);

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.status", is("NOT_STARTED")))
                .andExpect(jsonPath("$.priority", is("MEDIUM")))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.ownerId", is(testOwnerId.toString())));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void getTask_NonExistingTask_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(testTaskId))
                .thenThrow(new TaskNotFoundException(testTaskId));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void updateTask_ExistingTask_ReturnsUpdatedTask() throws Exception {
        Task updatedTask = new Task(
                testTaskId,
                "Updated Task",
                "Updated Description",
                LocalDateTime.now().plusDays(2),
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                Set.of("updated", "important"),
                testOwnerId
        );

        when(taskService.updateTask(eq(testTaskId), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/{taskId}", testTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.ownerId", is(testOwnerId.toString())));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void deleteTask_ExistingTask_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(testTaskId);

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void deleteTask_NonExistingTask_ReturnsNotFound() throws Exception {
        doThrow(new TaskNotFoundException(testTaskId))
                .when(taskService).deleteTask(testTaskId);

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_ValidParameters_ReturnsPagedResponse() throws Exception {
        Page<Task> page = new Page<>(
                List.of(testTask),
                1L,
                1,
                10,
                0
        );

        when(taskService.findTasks(
                anyString(), any(TaskStatus.class), any(TaskPriority.class), anyString(), eq(0), eq(10),
                argThat(sort -> sort.field() == SortField.DUE_DATE && sort.direction() == SortDirection.ASC)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dueDate,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.page", is(0)));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_InvalidPageSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "1001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_WithFilters_ReturnsFilteredTasks() throws Exception {
        Task filteredTask = new Task(
                UUID.randomUUID(),
                "Filtered Task",
                "Filtered Description",
                LocalDateTime.now().plusDays(1),
                TaskStatus.NOT_STARTED,
                TaskPriority.HIGH,
                Set.of("filtered", "important"),
                testOwnerId
        );

        Page<Task> page = new Page<>(
                List.of(filteredTask),
                1L,
                1,
                10,
                0
        );

        when(taskService.findTasks(
                eq("Filtered"),
                eq(TaskStatus.NOT_STARTED),
                eq(TaskPriority.HIGH),
                isNull(),
                eq(0),
                eq(10),
                any(SortOption.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("search", "Filtered")
                        .param("status", "NOT_STARTED")
                        .param("priority", "HIGH")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Filtered Task")))
                .andExpect(jsonPath("$.content[0].status", is("NOT_STARTED")))
                .andExpect(jsonPath("$.content[0].priority", is("HIGH")));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_InvalidSortField_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .param("sort", "invalidField,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Unknown sort field")));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_InvalidSortDirection_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .param("sort", "dueDate,invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Unknown sort direction")));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void findTasks_InvalidSortFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .param("sort", "invalid-format"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid sort parameter: Sort string must be in format 'field,direction'")));
    }
}
