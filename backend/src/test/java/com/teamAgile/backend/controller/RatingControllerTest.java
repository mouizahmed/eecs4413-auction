package com.teamAgile.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.teamAgile.backend.DTO.RatingRequestDTO;
import com.teamAgile.backend.model.Rating;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.RatingService;
import com.teamAgile.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Spy
    @InjectMocks
    private RatingController ratingController;

    private User testUser;
    private User ratedUser;
    private Rating testRating;
    private UUID testUserId;
    private UUID ratedUserId;
    private RatingRequestDTO ratingRequest;

    @BeforeEach
    void setUp() {
        // Set up test users
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setUserID(testUserId);
        testUser.setUsername("testuser");

        ratedUserId = UUID.randomUUID();
        ratedUser = new User();
        ratedUser.setUserID(ratedUserId);
        ratedUser.setUsername("rateduser");

        // Set up test rating - note we're mocking it now instead of creating directly
        testRating = mock(Rating.class);

        // Set up rating request DTO
        ratingRequest = new RatingRequestDTO();
        ratingRequest.setRatedUserId(ratedUserId);
        ratingRequest.setRating(4); // Changed to Integer
        ratingRequest.setFeedback("Great seller!");

        // Set up security context mock
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("testuser");

        // Mock base controller behavior
        lenient().when(userService.findByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    void createRating_Success() {
        // Arrange
        when(ratingService.createRating(eq(ratedUserId), eq(testUserId), eq(4), eq("Great seller!")))
                .thenReturn(testRating);

        // Mock the BaseController.getCurrentUser method using doReturn on spy
        doReturn(testUser).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        // Act
        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRating, response.getBody());
        verify(ratingService).createRating(eq(ratedUserId), eq(testUserId), eq(4), eq("Great seller!"));
    }

    @Test
    void createRating_Unauthorized() {
        // Arrange
        doReturn(null).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        // Act
        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(ratingService, never()).createRating(any(), any(), any(), any());
    }

    @Test
    void createRating_BadRequest() {
        // Arrange
        doReturn(testUser).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        when(ratingService.createRating(any(UUID.class), any(UUID.class), any(Integer.class), any(String.class)))
                .thenThrow(new IllegalArgumentException("Invalid rating"));

        // Act
        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserRatings_Success() {
        // Arrange
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);

        when(ratingService.getUserRatings(ratedUserId)).thenReturn(ratings);

        // Act
        ResponseEntity<List<Rating>> response = ratingController.getUserRatings(ratedUserId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ratings, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(ratingService).getUserRatings(ratedUserId);
    }

    @Test
    void getUserRatings_BadRequest() {
        // Arrange
        when(ratingService.getUserRatings(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act
        ResponseEntity<List<Rating>> response = ratingController.getUserRatings(ratedUserId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAverageUserRating_Success() {
        // Arrange
        double averageRating = 4.5;
        when(ratingService.getAverageUserRating(ratedUserId)).thenReturn(averageRating);

        // Act
        ResponseEntity<Double> response = ratingController.getAverageUserRating(ratedUserId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(averageRating, response.getBody());
        verify(ratingService).getAverageUserRating(ratedUserId);
    }

    @Test
    void getAverageUserRating_BadRequest() {
        // Arrange
        when(ratingService.getAverageUserRating(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act
        ResponseEntity<Double> response = ratingController.getAverageUserRating(ratedUserId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTotalUserRatings_Success() {
        // Arrange
        long totalRatings = 10L;
        when(ratingService.getTotalUserRatings(ratedUserId)).thenReturn(totalRatings);

        // Act
        ResponseEntity<Long> response = ratingController.getTotalUserRatings(ratedUserId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalRatings, response.getBody());
        verify(ratingService).getTotalUserRatings(ratedUserId);
    }

    @Test
    void getTotalUserRatings_BadRequest() {
        // Arrange
        when(ratingService.getTotalUserRatings(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act
        ResponseEntity<Long> response = ratingController.getTotalUserRatings(ratedUserId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}