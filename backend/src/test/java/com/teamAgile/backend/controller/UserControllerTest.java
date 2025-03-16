package com.teamAgile.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.DTO.SignInDTO;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.DTO.hateoas.UserModel;
import com.teamAgile.backend.DTO.hateoas.UserModelAssembler;
import com.teamAgile.backend.config.WebSocketConfig;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.UserService;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WebSecurityConfigurer.class,
				WebSocketConfigurer.class, WebSocketConfig.class }) })
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private UserModelAssembler userModelAssembler;

	@MockBean
	private AuctionService auctionService;

	private User testUser;
	private UUID userId;
	private SignUpDTO signUpDTO;
	private SignInDTO signInDTO;
	private ForgotPasswordDTO forgotPasswordDTO;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();

		testUser = new User();
		testUser.setUserID(userId);
		testUser.setFirstName("John");
		testUser.setLastName("Doe");
		testUser.setUsername("johndoe");

		try {
			java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
			passwordField.setAccessible(true);
			passwordField.set(testUser, "hashedPassword123");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set password field: " + e.getMessage());
		}

		Address address = new Address("Main St", 123, "12345", "New York", "USA");
		testUser.setAddress(address);

		testUser.setSecurityQuestion("What is your pet's name?");

		try {
			java.lang.reflect.Field securityAnswerField = User.class.getDeclaredField("securityAnswer");
			securityAnswerField.setAccessible(true);
			securityAnswerField.set(testUser, "Fluffy");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set securityAnswer field: " + e.getMessage());
		}

		signUpDTO = new SignUpDTO();

		try {
			java.lang.reflect.Field usernameField = SignInDTO.class.getDeclaredField("username");
			usernameField.setAccessible(true);
			usernameField.set(signUpDTO, "johndoe");

			java.lang.reflect.Field passwordField = SignInDTO.class.getDeclaredField("password");
			passwordField.setAccessible(true);
			passwordField.set(signUpDTO, "password123");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set up SignInDTO fields: " + e.getMessage());
		}

		try {
			java.lang.reflect.Field firstNameField = SignUpDTO.class.getDeclaredField("firstName");
			firstNameField.setAccessible(true);
			firstNameField.set(signUpDTO, "John");

			java.lang.reflect.Field lastNameField = SignUpDTO.class.getDeclaredField("lastName");
			lastNameField.setAccessible(true);
			lastNameField.set(signUpDTO, "Doe");

			java.lang.reflect.Field streetNameField = SignUpDTO.class.getDeclaredField("streetName");
			streetNameField.setAccessible(true);
			streetNameField.set(signUpDTO, "Main St");

			java.lang.reflect.Field streetNumField = SignUpDTO.class.getDeclaredField("streetNum");
			streetNumField.setAccessible(true);
			streetNumField.set(signUpDTO, 123);

			java.lang.reflect.Field postalCodeField = SignUpDTO.class.getDeclaredField("postalCode");
			postalCodeField.setAccessible(true);
			postalCodeField.set(signUpDTO, "12345");

			java.lang.reflect.Field cityField = SignUpDTO.class.getDeclaredField("city");
			cityField.setAccessible(true);
			cityField.set(signUpDTO, "New York");

			java.lang.reflect.Field countryField = SignUpDTO.class.getDeclaredField("country");
			countryField.setAccessible(true);
			countryField.set(signUpDTO, "USA");

			java.lang.reflect.Field securityQuestionField = SignUpDTO.class.getDeclaredField("securityQuestion");
			securityQuestionField.setAccessible(true);
			securityQuestionField.set(signUpDTO, "What is your pet's name?");

			java.lang.reflect.Field securityAnswerField = SignUpDTO.class.getDeclaredField("securityAnswer");
			securityAnswerField.setAccessible(true);
			securityAnswerField.set(signUpDTO, "Fluffy");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set up SignUpDTO: " + e.getMessage());
		}

		signInDTO = new SignInDTO();
		try {
			java.lang.reflect.Field usernameField = SignInDTO.class.getDeclaredField("username");
			usernameField.setAccessible(true);
			usernameField.set(signInDTO, "johndoe");

			java.lang.reflect.Field passwordField = SignInDTO.class.getDeclaredField("password");
			passwordField.setAccessible(true);
			passwordField.set(signInDTO, "password123");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set up SignInDTO: " + e.getMessage());
		}

		forgotPasswordDTO = new ForgotPasswordDTO();
		try {
			java.lang.reflect.Field securityAnswerField = ForgotPasswordDTO.class.getDeclaredField("securityAnswer");
			securityAnswerField.setAccessible(true);
			securityAnswerField.set(forgotPasswordDTO, "Fluffy");

			java.lang.reflect.Field newPasswordField = ForgotPasswordDTO.class.getDeclaredField("newPassword");
			newPasswordField.setAccessible(true);
			newPasswordField.set(forgotPasswordDTO, "newPassword123");
		} catch (Exception e) {
			throw new RuntimeException("Failed to set up ForgotPasswordDTO: " + e.getMessage());
		}
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testGetAllUsers() throws Exception {

		User user1 = new User();
		user1.setUserID(UUID.randomUUID());
		user1.setUsername("user1");

		User user2 = new User();
		user2.setUserID(UUID.randomUUID());
		user2.setUsername("user2");

		List<User> userList = Arrays.asList(user1, user2);
		when(userService.getAllUsers()).thenReturn(userList);
		when(userModelAssembler.toModel(any())).thenReturn(new UserModel(null));

		mockMvc.perform(get("/user/get-all").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content").isArray());
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSignUp_Success() throws Exception {

		when(userService.signUp(any(User.class))).thenReturn(testUser);

		mockMvc.perform(post("/user/sign-up").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpDTO)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User registered successfully"))
				.andExpect(jsonPath("$.data.username").value("johndoe"))
				.andExpect(jsonPath("$.data.firstName").value("John"))
				.andExpect(jsonPath("$.data.lastName").value("Doe"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSignUp_UsernameAlreadyTaken() throws Exception {

		when(userService.signUp(any(User.class))).thenThrow(
				new com.teamAgile.backend.exception.UsernameAlreadyExistsException("Username already taken"));

		mockMvc.perform(post("/user/sign-up").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpDTO)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Username already taken"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSignIn_Success() throws Exception {

		Authentication authentication = new UsernamePasswordAuthenticationToken("johndoe", "password123");
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		when(userService.signIn(anyString(), anyString())).thenReturn(testUser);

		// Act & Assert
		mockMvc.perform(post("/user/sign-in").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signInDTO)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Login successful"))
				.andExpect(jsonPath("$.data.username").value("johndoe"))
				.andExpect(jsonPath("$.data.firstName").value("John"))
				.andExpect(jsonPath("$.data.lastName").value("Doe"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSignIn_InvalidCredentials() throws Exception {

		when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(
				new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

		// Act & Assert
		mockMvc.perform(post("/user/sign-in").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signInDTO)))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid username or password"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSignOut_Success() throws Exception {

		mockMvc.perform(post("/user/sign-out").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Successfully signed out"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testGetSecurityQuestion_Success() throws Exception {

		when(userService.findSecurityQuestionByUsername("johndoe")).thenReturn("What is your pet's name?");

		mockMvc.perform(get("/user/get-security-question").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("username", "johndoe")).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value("What is your pet's name?"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testGetSecurityQuestion_UserNotFound() throws Exception {

		when(userService.findSecurityQuestionByUsername("nonexistent"))
				.thenThrow(new com.teamAgile.backend.exception.UserNotFoundException("User not found"));

		mockMvc.perform(get("/user/get-security-question").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("username", "nonexistent")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("User not found"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testValidateSecurityAnswer_Success() throws Exception {

		when(userService.validateSecurityAnswer(anyString(), any(ForgotPasswordDTO.class))).thenReturn(true);

		mockMvc.perform(post("/user/forgot-password").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("username", "johndoe").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(forgotPasswordDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Password reset successful"))
				.andExpect(jsonPath("$.data").value(true));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testValidateSecurityAnswer_Failure() throws Exception {

		when(userService.validateSecurityAnswer(anyString(), any(ForgotPasswordDTO.class))).thenReturn(false);

		mockMvc.perform(post("/user/forgot-password").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("username", "johndoe").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(forgotPasswordDTO))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid security answer"));
	}
}