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
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setUserID(testUserId);
        testUser.setUsername("testuser");

        ratedUserId = UUID.randomUUID();
        ratedUser = new User();
        ratedUser.setUserID(ratedUserId);
        ratedUser.setUsername("rateduser");

        testRating = mock(Rating.class);

        ratingRequest = new RatingRequestDTO();
        ratingRequest.setRatedUserId(ratedUserId);
        ratingRequest.setRating(4);
        ratingRequest.setFeedback("Great seller!");

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        lenient().when(userService.findByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    void createRating_Success() {
        when(ratingService.createRating(eq(ratedUserId), eq(testUserId), eq(4), eq("Great seller!")))
                .thenReturn(testRating);

        doReturn(testUser).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRating, response.getBody());
        verify(ratingService).createRating(eq(ratedUserId), eq(testUserId), eq(4), eq("Great seller!"));
    }

    @Test
    void createRating_Unauthorized() {
        doReturn(null).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(ratingService, never()).createRating(any(), any(), any(), any());
    }

    @Test
    void createRating_BadRequest() {
        doReturn(testUser).when(ratingController).getCurrentUser(any(HttpServletRequest.class));

        when(ratingService.createRating(any(UUID.class), any(UUID.class), any(Integer.class), any(String.class)))
                .thenThrow(new IllegalArgumentException("Invalid rating"));

        ResponseEntity<Rating> response = ratingController.createRating(ratingRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserRatings_Success() {
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);

        when(ratingService.getUserRatings(ratedUserId)).thenReturn(ratings);

        ResponseEntity<List<Rating>> response = ratingController.getUserRatings(ratedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ratings, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(ratingService).getUserRatings(ratedUserId);
    }

    @Test
    void getUserRatings_BadRequest() {
        when(ratingService.getUserRatings(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<List<Rating>> response = ratingController.getUserRatings(ratedUserId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAverageUserRating_Success() {
        double averageRating = 4.5;
        when(ratingService.getAverageUserRating(ratedUserId)).thenReturn(averageRating);

        ResponseEntity<Double> response = ratingController.getAverageUserRating(ratedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(averageRating, response.getBody());
        verify(ratingService).getAverageUserRating(ratedUserId);
    }

    @Test
    void getAverageUserRating_BadRequest() {
        when(ratingService.getAverageUserRating(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<Double> response = ratingController.getAverageUserRating(ratedUserId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTotalUserRatings_Success() {
        long totalRatings = 10L;
        when(ratingService.getTotalUserRatings(ratedUserId)).thenReturn(totalRatings);

        ResponseEntity<Long> response = ratingController.getTotalUserRatings(ratedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalRatings, response.getBody());
        verify(ratingService).getTotalUserRatings(ratedUserId);
    }

    @Test
    void getTotalUserRatings_BadRequest() {
        when(ratingService.getTotalUserRatings(ratedUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<Long> response = ratingController.getTotalUserRatings(ratedUserId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}