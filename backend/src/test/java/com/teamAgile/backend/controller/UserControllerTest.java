package com.teamAgile.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.teamAgile.backend.config.WebSocketConfig;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.UserService;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WebSecurityConfigurer.class,
                WebSocketConfigurer.class, WebSocketConfig.class })
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User testUser;
    private UUID userId;
    private SignUpDTO signUpDTO;
    private SignInDTO signInDTO;
    private ForgotPasswordDTO forgotPasswordDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Create a test user
        testUser = new User();
        testUser.setUserID(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("johndoe");

        // Use reflection to set the password field directly to avoid calling the real
        // setPassword method
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

        // Use reflection to set the securityAnswer field directly
        try {
            java.lang.reflect.Field securityAnswerField = User.class.getDeclaredField("securityAnswer");
            securityAnswerField.setAccessible(true);
            securityAnswerField.set(testUser, "Fluffy");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set securityAnswer field: " + e.getMessage());
        }

        // Create SignUpDTO
        signUpDTO = new SignUpDTO();
        // Set SignInDTO fields (username and password)
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

        // Set SignUpDTO specific fields
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

        // Create SignInDTO
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

        // Create ForgotPasswordDTO
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
        // Arrange
        User user1 = new User();
        user1.setUserID(UUID.randomUUID());
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUserID(UUID.randomUUID());
        user2.setUsername("user2");

        List<User> userList = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/user/get-all")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSignUp_Success() throws Exception {
        // Arrange
        when(userService.signUp(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/user/sign-up")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSignUp_UsernameAlreadyTaken() throws Exception {
        // Arrange
        when(userService.signUp(any(User.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/user/sign-up")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already taken."));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSignIn_Success() throws Exception {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken("johndoe", "password123");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userService.signIn(anyString(), anyString())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/user/sign-in")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSignIn_InvalidCredentials() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(
                new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/user/sign-in")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid username or password."));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSignOut_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/user/sign-out")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully signed out"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetSecurityQuestion_Success() throws Exception {
        // Arrange
        when(userService.findSecurityQuestionByUsername("johndoe")).thenReturn("What is your pet's name?");

        // Act & Assert
        mockMvc.perform(get("/user/get-security-question")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "johndoe"))
                .andExpect(status().isOk())
                .andExpect(content().string("What is your pet's name?"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetSecurityQuestion_UserNotFound() throws Exception {
        // Arrange
        when(userService.findSecurityQuestionByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/user/get-security-question")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testValidateSecurityAnswer_Success() throws Exception {
        // Arrange
        when(userService.validateSecurityAnswer(anyString(), any(ForgotPasswordDTO.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/user/forgot-password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testValidateSecurityAnswer_Failure() throws Exception {
        // Arrange
        when(userService.validateSecurityAnswer(anyString(), any(ForgotPasswordDTO.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/user/forgot-password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}