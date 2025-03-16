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
import com.teamAgile.backend.exception.UserNotFoundException;
import com.teamAgile.backend.exception.UsernameAlreadyExistsException;
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

		testUser = new User();
		testUser.setUserID(userId);
		testUser.setFirstName("John");
		testUser.setLastName("Doe");
		testUser.setUsername("johndoe");
		testUser.setPassword("password123");

		Address address = new Address("Main St", 123, "12345", "New York", "USA");
		testUser.setAddress(address);

		testUser.setSecurityQuestion("What is your pet's name?");
		testUser.setSecurityAnswer("Fluffy");
	}

	@Test
	void testSignUp_Success() {

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User result = userService.signUp(testUser);

		assertNotNull(result);
		assertEquals(testUser.getUserID(), result.getUserID());
		assertEquals(testUser.getUsername(), result.getUsername());
		verify(userRepository, times(1)).findByUsername(testUser.getUsername());
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	void testSignUp_UsernameAlreadyExists() {

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

		assertThrows(UsernameAlreadyExistsException.class, () -> {
			userService.signUp(testUser);
		});
		verify(userRepository, times(1)).findByUsername(testUser.getUsername());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testSignIn_Success() {

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

		try (org.mockito.MockedStatic<BCryptHashing> mockedBCrypt = mockStatic(BCryptHashing.class)) {
			mockedBCrypt.when(() -> BCryptHashing.checkPassword(rawPassword, hashedPassword)).thenReturn(true);

			User result = userService.signIn("johndoe", rawPassword);

			assertNotNull(result);
			assertEquals(userId, result.getUserID());
			assertEquals("johndoe", result.getUsername());
		}
	}

	@Test
	void testSignIn_InvalidUsername() {

		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.signIn("nonexistent", "password123");
		});
		verify(userRepository, times(1)).findByUsername("nonexistent");
	}

	@Test
	void testSignIn_InvalidPassword() {

		when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

		try (org.mockito.MockedStatic<BCryptHashing> mockedBCrypt = mockStatic(BCryptHashing.class)) {
			mockedBCrypt.when(() -> BCryptHashing.checkPassword(anyString(), anyString())).thenReturn(false);

			User result = userService.signIn("johndoe", "wrongpassword");

			assertNull(result);
		}
	}

	@Test
	void testGetAllUsers() {

		User user1 = new User();
		user1.setUserID(UUID.randomUUID());
		user1.setUsername("user1");

		User user2 = new User();
		user2.setUserID(UUID.randomUUID());
		user2.setUsername("user2");

		List<User> userList = Arrays.asList(user1, user2);
		when(userRepository.findAll()).thenReturn(userList);

		List<User> result = userService.getAllUsers();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getUsername());
		assertEquals("user2", result.get(1).getUsername());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testFindSecurityQuestionByUsername_Success() {

		when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

		String result = userService.findSecurityQuestionByUsername("johndoe");

		assertNotNull(result);
		assertEquals("What is your pet's name?", result);
		verify(userRepository, times(1)).findByUsername("johndoe");
	}

	@Test
	void testFindSecurityQuestionByUsername_UserNotFound() {

		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findSecurityQuestionByUsername("nonexistent");
		});
		verify(userRepository, times(1)).findByUsername("nonexistent");
	}

	@Test
	void testValidateSecurityAnswer_Success() {

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

		boolean result = userService.validateSecurityAnswer("johndoe", dto);

		assertTrue(result);
		verify(userRepository, times(1)).findByUsername("johndoe");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testValidateSecurityAnswer_UserNotFound() {

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

		assertThrows(UserNotFoundException.class, () -> {
			userService.validateSecurityAnswer("nonexistent", dto);
		});
		verify(userRepository, times(1)).findByUsername("nonexistent");
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testValidateSecurityAnswer_IncorrectAnswer() {

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

		boolean result = userService.validateSecurityAnswer("johndoe", dto);

		assertFalse(result);
		verify(userRepository, times(1)).findByUsername("johndoe");
		verify(userRepository, never()).save(any(User.class));
	}
}