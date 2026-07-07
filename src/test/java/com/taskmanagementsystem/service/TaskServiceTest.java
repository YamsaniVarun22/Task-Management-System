package com.taskmanagementsystem.service;

import com.taskmanagementsystem.dto.TaskDto;
import com.taskmanagementsystem.enums.Role;
import com.taskmanagementsystem.enums.Status;
import com.taskmanagementsystem.exception.TaskNotFoundException;
import com.taskmanagementsystem.exception.UnauthorizedTaskAccessException;
import com.taskmanagementsystem.exception.UserNotFoundException;
import com.taskmanagementsystem.mapper.TaskMapper;
import com.taskmanagementsystem.model.Task;
import com.taskmanagementsystem.model.User;
import com.taskmanagementsystem.repository.TaskRepository;
import com.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskDto testTaskDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Status.PENDING);
        testTask.setDueDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        testTask.setUser(testUser);

        testTaskDto = TaskMapper.toDTO(testTask);
    }

    // Helper method to mock security context
    private void mockSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUserName("testuser")).thenReturn(testUser);
    }

    // ==================== saveTask Tests ====================
    @Test
    @DisplayName("Should save task successfully for current user")
    void testSaveTaskSuccess() {
        mockSecurityContext();

        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(Status.PENDING);
        newTask.setDueDate(new Date(System.currentTimeMillis() + 86400000));

        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskDto result = taskService.saveTask(newTask);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when authenticated user not found")
    void testSaveTaskUserNotFound() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownuser");
        when(userRepository.findByUserName("unknownuser")).thenReturn(null);

        Task newTask = new Task();
        newTask.setTitle("New Task");

        assertThrows(UserNotFoundException.class, () -> taskService.saveTask(newTask));
    }

    // ==================== getTaskById Tests ====================
    @Test
    @DisplayName("Should get task by ID successfully for authorized user")
    void testGetTaskByIdSuccess() {
        mockSecurityContext();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task does not exist")
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    @DisplayName("Should throw UnauthorizedTaskAccessException when user not task owner")
    void testGetTaskByIdUnauthorized() {
        mockSecurityContext();

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUserName("otheruser");

        Task otherUserTask = new Task();
        otherUserTask.setId(1L);
        otherUserTask.setTitle("Other User Task");
        otherUserTask.setUser(otherUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherUserTask));

        assertThrows(UnauthorizedTaskAccessException.class, () -> taskService.getTaskById(1L));
    }

    // ==================== getAllTasks Tests ====================
    @Test
    @DisplayName("Should get all tasks for current user")
    void testGetAllTasksSuccess() {
        mockSecurityContext();

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setUser(testUser);

        List<Task> tasks = new ArrayList<>();
        tasks.add(testTask);
        tasks.add(task2);

        when(taskRepository.findAllByUser(testUser)).thenReturn(tasks);

        List<TaskDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAllByUser(testUser);
    }

    @Test
    @DisplayName("Should return empty list when user has no tasks")
    void testGetAllTasksEmpty() {
        mockSecurityContext();
        when(taskRepository.findAllByUser(testUser)).thenReturn(new ArrayList<>());

        List<TaskDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findAllByUser(testUser);
    }

    // ==================== updateTaskById Tests ====================
    @Test
    @DisplayName("Should update task successfully for authorized user")
    void testUpdateTaskByIdSuccess() {
        mockSecurityContext();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setDueDate(new Date(System.currentTimeMillis() + 172800000));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Updated Title");
        savedTask.setDescription("Updated Description");
        savedTask.setStatus(Status.IN_PROGRESS);
        savedTask.setUser(testUser);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDto result = taskService.updateTaskById(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when updating non-existent task")
    void testUpdateTaskByIdNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskById(999L, updatedTask));
    }

    @Test
    @DisplayName("Should throw UnauthorizedTaskAccessException when updating other user's task")
    void testUpdateTaskByIdUnauthorized() {
        mockSecurityContext();

        User otherUser = new User();
        otherUser.setId(2L);

        Task otherUserTask = new Task();
        otherUserTask.setId(1L);
        otherUserTask.setUser(otherUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherUserTask));

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");

        assertThrows(UnauthorizedTaskAccessException.class, () -> taskService.updateTaskById(1L, updatedTask));
    }

    // ==================== deleteTaskById Tests ====================
    @Test
    @DisplayName("Should delete task successfully for authorized user")
    void testDeleteTaskByIdSuccess() {
        mockSecurityContext();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        String result = taskService.deleteTaskById(1L);

        assertEquals("Succesfully removed Task", result);
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when deleting non-existent task")
    void testDeleteTaskByIdNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(999L));
    }

    @Test
    @DisplayName("Should throw UnauthorizedTaskAccessException when deleting other user's task")
    void testDeleteTaskByIdUnauthorized() {
        mockSecurityContext();

        User otherUser = new User();
        otherUser.setId(2L);

        Task otherUserTask = new Task();
        otherUserTask.setId(1L);
        otherUserTask.setUser(otherUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherUserTask));

        assertThrows(UnauthorizedTaskAccessException.class, () -> taskService.deleteTaskById(1L));
    }

    // ==================== markAsCompleted Tests ====================
    @Test
    @DisplayName("Should mark task as completed successfully")
    void testMarkAsCompletedSuccess() {
        mockSecurityContext();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setTitle("Test Task");
        completedTask.setDescription("Test Description");
        completedTask.setStatus(Status.COMPLETED);
        completedTask.setUser(testUser);

        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        TaskDto result = taskService.markAsCompleted(1L);

        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when marking non-existent task as completed")
    void testMarkAsCompletedNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.markAsCompleted(999L));
    }

    @Test
    @DisplayName("Should throw UnauthorizedTaskAccessException when marking other user's task as completed")
    void testMarkAsCompletedUnauthorized() {
        mockSecurityContext();

        User otherUser = new User();
        otherUser.setId(2L);

        Task otherUserTask = new Task();
        otherUserTask.setId(1L);
        otherUserTask.setStatus(Status.PENDING);
        otherUserTask.setUser(otherUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherUserTask));

        assertThrows(UnauthorizedTaskAccessException.class, () -> taskService.markAsCompleted(1L));
    }
}

