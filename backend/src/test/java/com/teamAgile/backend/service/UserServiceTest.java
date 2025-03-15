package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.UserRepository;
import com.teamAgile.backend.util.BCryptHashing;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Create a test user
        testUser = new User();
        testUser.setUserID(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("johndoe");
        testUser.setPassword("password123"); // This will be hashed by the setter

        Address address = new Address("Main St", 123, "12345", "New York", "USA");
        testUser.setAddress(address);

        testUser.setSecurityQuestion("What is your pet's name?");
        testUser.setSecurityAnswer("Fluffy");
    }

    @Test
    void testSignUp_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.signUp(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUserID(), result.getUserID());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testSignUp_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.signUp(testUser);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignIn_Success() {
        // Arrange
        String rawPassword = "password123";
        String hashedPassword = BCryptHashing.hashPassword(rawPassword);

        User userWithHashedPassword = new User();
        userWithHashedPassword.setUserID(userId);
        userWithHashedPassword.setUsername("johndoe");
        // Set the password directly to simulate the hashed password in the database
        try {
            java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(userWithHashedPassword, hashedPassword);
        } catch (Exception e) {
            fail("Failed to set hashed password: " + e.getMessage());
        }

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(userWithHashedPassword));

        // Mock the BCryptHashing.checkPassword method
        try (org.mockito.MockedStatic<BCryptHashing> mockedBCrypt = mockStatic(BCryptHashing.class)) {
            mockedBCrypt.when(() -> BCryptHashing.checkPassword(rawPassword, hashedPassword)).thenReturn(true);

            // Act
            User result = userService.signIn("johndoe", rawPassword);

            // Assert
            assertNotNull(result);
            assertEquals(userId, result.getUserID());
            assertEquals("johndoe", result.getUsername());
        }
    }

    @Test
    void testSignIn_InvalidUsername() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        User result = userService.signIn("nonexistent", "password123");

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testSignIn_InvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // Mock the BCryptHashing.checkPassword method to return false
        try (org.mockito.MockedStatic<BCryptHashing> mockedBCrypt = mockStatic(BCryptHashing.class)) {
            mockedBCrypt.when(() -> BCryptHashing.checkPassword(anyString(), anyString())).thenReturn(false);

            // Act
            User result = userService.signIn("johndoe", "wrongpassword");

            // Assert
            assertNull(result);
        }
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setUserID(UUID.randomUUID());
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUserID(UUID.randomUUID());
        user2.setUsername("user2");

        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindSecurityQuestionByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.findSecurityQuestionByUsername("johndoe");

        // Assert
        assertNotNull(result);
        assertEquals("What is your pet's name?", result);
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testFindSecurityQuestionByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        String result = userService.findSecurityQuestionByUsername("nonexistent");

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testValidateSecurityAnswer_Success() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        try {
            java.lang.reflect.Field securityAnswerField = ForgotPasswordDTO.class.getDeclaredField("securityAnswer");
            securityAnswerField.setAccessible(true);
            securityAnswerField.set(dto, "Fluffy");

            java.lang.reflect.Field newPasswordField = ForgotPasswordDTO.class.getDeclaredField("newPassword");
            newPasswordField.setAccessible(true);
            newPasswordField.set(dto, "newPassword123");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.validateSecurityAnswer("johndoe", dto);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("johndoe");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testValidateSecurityAnswer_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        try {
            java.lang.reflect.Field securityAnswerField = ForgotPasswordDTO.class.getDeclaredField("securityAnswer");
            securityAnswerField.setAccessible(true);
            securityAnswerField.set(dto, "Fluffy");

            java.lang.reflect.Field newPasswordField = ForgotPasswordDTO.class.getDeclaredField("newPassword");
            newPasswordField.setAccessible(true);
            newPasswordField.set(dto, "newPassword123");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        // Act
        boolean result = userService.validateSecurityAnswer("nonexistent", dto);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testValidateSecurityAnswer_IncorrectAnswer() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        try {
            java.lang.reflect.Field securityAnswerField = ForgotPasswordDTO.class.getDeclaredField("securityAnswer");
            securityAnswerField.setAccessible(true);
            securityAnswerField.set(dto, "WrongAnswer");

            java.lang.reflect.Field newPasswordField = ForgotPasswordDTO.class.getDeclaredField("newPassword");
            newPasswordField.setAccessible(true);
            newPasswordField.set(dto, "newPassword123");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        // Act
        boolean result = userService.validateSecurityAnswer("johndoe", dto);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }
}