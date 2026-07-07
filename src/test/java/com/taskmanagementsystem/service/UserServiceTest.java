package com.taskmanagementsystem.service;

import com.taskmanagementsystem.dto.UserDto;
import com.taskmanagementsystem.enums.Role;
import com.taskmanagementsystem.exception.UserAlreadyExistException;
import com.taskmanagementsystem.exception.UserNotFoundException;
import com.taskmanagementsystem.mapper.UserMapper;
import com.taskmanagementsystem.model.User;
import com.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("EncodedPassword123");
        testUser.setRole(Role.USER);

        testUserDto = UserMapper.toDTO(testUser);
    }

    // ==================== saveUser Tests ====================
    @Test
    @DisplayName("Should save new user successfully")
    void testSaveUserSuccess() {
        User newUser = new User();
        newUser.setUserName("newuser");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("EncodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        String result = userService.saveUser(newUser);

        assertNotNull(result);
        assertTrue(result.contains("newuser"));
        assertTrue(result.contains("registered successfully"));
        verify(userRepository, times(1)).existsByUserName("newuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistException when username already exists")
    void testSaveUserAlreadyExists() {
        User newUser = new User();
        newUser.setUserName("existinguser");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        when(userRepository.existsByUserName("existinguser")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.saveUser(newUser));
        verify(userRepository, times(1)).existsByUserName("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void testSaveUserPasswordEncoded() {
        User newUser = new User();
        newUser.setUserName("newuser");
        newUser.setPassword("plainPassword");
        newUser.setRole(Role.USER);

        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("EncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.saveUser(newUser);

        verify(passwordEncoder, times(1)).encode("plainPassword");
    }

    // ==================== getUserByUserId Tests ====================
    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserByUserIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserByUserId(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user ID does not exist")
    void testGetUserByUserIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUserId(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    // ==================== getUserByUserName Tests ====================
    @Test
    @DisplayName("Should get user by username successfully")
    void testGetUserByUserNameSuccess() {
        when(userRepository.findByUserName("testuser")).thenReturn(testUser);

        UserDto result = userService.getUserByUserName("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when username does not exist")
    void testGetUserByUserNameNotFound() {
        when(userRepository.findByUserName("nonexistent")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUserName("nonexistent"));
        verify(userRepository, times(1)).findByUserName("nonexistent");
    }

    // ==================== getAllUsers Tests ====================
    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsersSuccess() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("user2");
        user2.setPassword("password456");
        user2.setRole(Role.ADMIN);

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUserName());
        assertEquals("user2", result.get(1).getUserName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // ==================== updateUser Tests ====================
    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUserSuccess() {
        User updatedUser = new User();
        updatedUser.setUserName("updatedusername");
        updatedUser.setPassword("newpassword123");
        updatedUser.setRole(Role.ADMIN);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("updatedusername");
        savedUser.setPassword("EncodedNewPassword");
        savedUser.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserName("updatedusername")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("EncodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("updatedusername", result.getUserName());
        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newpassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void testUpdateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setUserName("newusername");
        updatedUser.setPassword("password123");
        updatedUser.setRole(Role.USER);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(999L, updatedUser));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistException when updating to existing username")
    void testUpdateUserUsernameAlreadyExists() {
        User updatedUser = new User();
        updatedUser.setUserName("existingusername");
        updatedUser.setPassword("password123");
        updatedUser.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserName("existingusername")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.updateUser(1L, updatedUser));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByUserName("existingusername");
    }

    @Test
    @DisplayName("Should allow updating user with same username")
    void testUpdateUserSameUsername() {
        User updatedUser = new User();
        updatedUser.setUserName("testuser");
        updatedUser.setPassword("newpassword123");
        updatedUser.setRole(Role.ADMIN);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("testuser");
        savedUser.setPassword("EncodedNewPassword");
        savedUser.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserName("testuser")).thenReturn(true);
        when(passwordEncoder.encode("newpassword123")).thenReturn("EncodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ==================== deleteUser Tests ====================
    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        String result = userService.deleteUser(1L);

        assertEquals("Successfully removed user", result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void testDeleteUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }

    // ==================== loadUserByUsername Tests ====================
    @Test
    @DisplayName("Should load user details by username successfully")
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUserName("testuser")).thenReturn(testUser);

        UserDetails result = userService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("EncodedPassword123", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    @DisplayName("Should load admin user with ROLE_ADMIN authority")
    void testLoadAdminUserByUsername() {
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUserName("admin");
        adminUser.setPassword("AdminPassword");
        adminUser.setRole(Role.ADMIN);

        when(userRepository.findByUserName("admin")).thenReturn(adminUser);

        UserDetails result = userService.loadUserByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository, times(1)).findByUserName("admin");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUserName("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUserName("nonexistent");
    }

    // ==================== Integration Tests ====================
    @Test
    @DisplayName("Should complete user lifecycle: create, retrieve, update, delete")
    void testUserLifecycle() {
        // Create user
        User newUser = new User();
        newUser.setUserName("lifecycleuser");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        when(userRepository.existsByUserName("lifecycleuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("EncodedPassword");
        User createdUser = new User();
        createdUser.setId(5L);
        createdUser.setUserName("lifecycleuser");
        createdUser.setPassword("EncodedPassword");
        createdUser.setRole(Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        String createResult = userService.saveUser(newUser);
        assertTrue(createResult.contains("registered successfully"));

        // Retrieve user
        when(userRepository.findById(5L)).thenReturn(Optional.of(createdUser));
        UserDto retrievedUser = userService.getUserByUserId(5L);
        assertEquals("lifecycleuser", retrievedUser.getUserName());

        // Update user
        User updatedData = new User();
        updatedData.setUserName("updatedlifecycleuser");
        updatedData.setPassword("newpassword456");
        updatedData.setRole(Role.ADMIN);

        when(userRepository.existsByUserName("updatedlifecycleuser")).thenReturn(false);
        when(passwordEncoder.encode("newpassword456")).thenReturn("EncodedNewPassword");
        User updatedUser = new User();
        updatedUser.setId(5L);
        updatedUser.setUserName("updatedlifecycleuser");
        updatedUser.setPassword("EncodedNewPassword");
        updatedUser.setRole(Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto updated = userService.updateUser(5L, updatedData);
        assertEquals("updatedlifecycleuser", updated.getUserName());
        assertEquals(Role.ADMIN, updated.getRole());

        // Delete user
        when(userRepository.existsById(5L)).thenReturn(true);
        String deleteResult = userService.deleteUser(5L);
        assertEquals("Successfully removed user", deleteResult);
        verify(userRepository, times(1)).deleteById(5L);
    }
}

