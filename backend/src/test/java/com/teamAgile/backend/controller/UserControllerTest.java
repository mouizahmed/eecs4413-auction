package com.teamAgile.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.teamAgile.backend.DTO.ApiResponse;
import com.teamAgile.backend.DTO.AuctionItemResponseDTO;
import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.DTO.SignInDTO;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.DTO.UserResponseDTO;
import com.teamAgile.backend.DTO.hateoas.UserModel;
import com.teamAgile.backend.DTO.hateoas.UserModelAssembler;
import com.teamAgile.backend.config.JwtUtils;
import com.teamAgile.backend.exception.UserNotFoundException;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserModelAssembler userModelAssembler;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Spy
    @InjectMocks
    private UserController userController;

    private User testUser;
    private UUID testUserId;
    private UserResponseDTO userResponseDTO;
    private UserModel userModel;
    private List<User> userList;
    private List<AuctionItem> unpaidItems;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setUserID(testUserId);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setSecurityQuestion("What is your pet's name?");
        testUser.setSecurityAnswer("Fluffy");

        // Create address
        Address address = new Address("Main St", 123, "A1B2C3", "City", "Province", "Country");
        testUser.setAddress(address);

        // Setup user response DTO
        userResponseDTO = new UserResponseDTO(testUser);

        // Setup user model
        userModel = mock(UserModel.class);

        // Setup user list
        userList = new ArrayList<>();
        userList.add(testUser);

        // Setup unpaid items
        unpaidItems = new ArrayList<>();
        AuctionItem unpaidItem = mock(ForwardAuctionItem.class);
        lenient().when(unpaidItem.getItemID()).thenReturn(UUID.randomUUID());
        lenient().when(unpaidItem.getItemName()).thenReturn("Unpaid Item");
        unpaidItems.add(unpaidItem);

        // Setup security context
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("testuser");

        // Setup userService mock
        lenient().when(userService.findByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(userList);
        when(userModelAssembler.toModel(any(UserResponseDTO.class))).thenReturn(userModel);

        List<UserModel> userModels = new ArrayList<>();
        userModels.add(userModel);
        CollectionModel<UserModel> collectionModel = CollectionModel.of(userModels);

        // Act
        ResponseEntity<ApiResponse<CollectionModel<UserModel>>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        // Verify service was called
        verify(userService).getAllUsers();
    }

    @Test
    void signUp_Success() {
        // Arrange
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setUsername("newuser");
        signUpDTO.setPassword("password123");
        signUpDTO.setFirstName("New");
        signUpDTO.setLastName("User");
        signUpDTO.setSecurityQuestion("What is your pet's name?");
        signUpDTO.setSecurityAnswer("Fido");
        signUpDTO.setStreetName("Main St");
        signUpDTO.setStreetNum(123);
        signUpDTO.setPostalCode("A1B2C3");
        signUpDTO.setCity("City");
        signUpDTO.setProvince("Province");
        signUpDTO.setCountry("Country");

        when(userService.signUp(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.signUp(signUpDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User registered successfully", response.getBody().getMessage());

        // Verify service was called
        verify(userService).signUp(any(User.class));
    }

    @Test
    void signUp_ValidationError() {
        // Arrange
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setUsername("newuser");
        signUpDTO.setPassword("short"); // Too short password

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.signUp(signUpDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        // Verify service was not called
        verify(userService, never()).signUp(any(User.class));
    }

    @Test
    void signIn_Success() {
        // Arrange
        SignInDTO signInDTO = new SignInDTO();
        signInDTO.setUsername("testuser");
        signInDTO.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userService.signIn("testuser", "password")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.signIn(signInDTO, request, httpResponse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Login successful", response.getBody().getMessage());

        // Verify JWT token was added as a cookie
        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
        verify(httpResponse, times(1)).addHeader(eq("Set-Cookie"), contains("jwt=jwt-token"));
    }

    @Test
    void signIn_InvalidCredentials() {
        // Arrange
        SignInDTO signInDTO = new SignInDTO();
        signInDTO.setUsername("testuser");
        signInDTO.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.signIn(signInDTO, request, httpResponse);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void signOut_Success() {
        // Act
        ResponseEntity<ApiResponse<Void>> response = userController.signOut(request, httpResponse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Successfully signed out", response.getBody().getMessage());

        // Verify cookie was invalidated
        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void getSecurityQuestion_Success() {
        // Arrange
        String username = "testuser";
        String securityQuestion = "What is your pet's name?";

        when(userService.findSecurityQuestionByUsername(username)).thenReturn(securityQuestion);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.getSecurityQuestion(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(securityQuestion, response.getBody().getData());

        // Verify service was called
        verify(userService).findSecurityQuestionByUsername(username);
    }

    @Test
    void getSecurityQuestion_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";

        when(userService.findSecurityQuestionByUsername(username))
                .thenThrow(new UserNotFoundException("User not found with username: " + username));

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.getSecurityQuestion(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void validateSecurityAnswer_Success() {
        // Arrange
        String username = "testuser";
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setSecurityAnswer("Fluffy");
        forgotPasswordDTO.setNewPassword("newpassword123");

        when(userService.validateSecurityAnswer(username, forgotPasswordDTO)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = userController.validateSecurityAnswer(username,
                forgotPasswordDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Password reset successful", response.getBody().getMessage());
        assertTrue(response.getBody().getData());

        // Verify service was called
        verify(userService).validateSecurityAnswer(username, forgotPasswordDTO);
    }

    @Test
    void validateSecurityAnswer_InvalidAnswer() {
        // Arrange
        String username = "testuser";
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setSecurityAnswer("WrongAnswer");
        forgotPasswordDTO.setNewPassword("newpassword123");

        when(userService.validateSecurityAnswer(username, forgotPasswordDTO)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = userController.validateSecurityAnswer(username,
                forgotPasswordDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid security answer", response.getBody().getMessage());
    }

    @Test
    void getUnpaidItems_Success() {
        // Arrange
        doReturn(testUser).when(userController).getCurrentUser(any(HttpServletRequest.class));
        when(auctionService.getUnpaidItemsForUser(testUser)).thenReturn(unpaidItems);

        // Use MockedStatic to handle the static method behavior
        try (MockedStatic<AuctionItemResponseDTO> mockedStatic = mockStatic(AuctionItemResponseDTO.class)) {
            // Mock the static method
            AuctionItemResponseDTO mockResponseDTO = mock(AuctionItemResponseDTO.class);
            mockedStatic.when(() -> AuctionItemResponseDTO.fromAuctionItem(any(AuctionItem.class)))
                    .thenReturn(mockResponseDTO);

            // Act
            ResponseEntity<ApiResponse<List<AuctionItemResponseDTO>>> response = userController.getUnpaidItems(request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isSuccess());
            assertEquals("Retrieved unpaid items successfully", response.getBody().getMessage());

            // Verify service was called
            verify(auctionService).getUnpaidItemsForUser(testUser);
        }
    }

    @Test
    void getUnpaidItems_Unauthorized() {
        // Arrange
        doReturn(null).when(userController).getCurrentUser(any(HttpServletRequest.class));

        // Act
        ResponseEntity<ApiResponse<List<AuctionItemResponseDTO>>> response = userController.getUnpaidItems(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        // Verify service was not called
        verify(auctionService, never()).getUnpaidItemsForUser(any(User.class));
    }

    @Test
    void getCurrentUserInfo_Success() {
        // Arrange
        doReturn(testUser).when(userController).getCurrentUser(any(HttpServletRequest.class));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getCurrentUserInfo(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void getCurrentUserInfo_Unauthorized() {
        // Arrange
        doReturn(null).when(userController).getCurrentUser(any(HttpServletRequest.class));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getCurrentUserInfo(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void getUserByUsername_Success() {
        // Arrange
        String username = "testuser";
        when(userService.findByUsername(username)).thenReturn(testUser);

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserByUsername(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());

        // Verify service was called
        verify(userService).findByUsername(username);
    }

    @Test
    void getUserByUsername_NotFound() {
        // Arrange
        String username = "nonexistentuser";
        when(userService.findByUsername(username))
                .thenThrow(new UserNotFoundException("User not found with username: " + username));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserByUsername(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void getUserById_Success() {
        // Arrange
        String userId = testUserId.toString();
        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());

        // Verify service was called
        verify(userService).getUserById(any(UUID.class));
    }

    @Test
    void getUserById_InvalidUUID() {
        // Arrange
        String userId = "not-a-uuid";

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        // Verify service was not called
        verify(userService, never()).getUserById(any(UUID.class));
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new UserNotFoundException("User not found with ID: " + userId));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }
}