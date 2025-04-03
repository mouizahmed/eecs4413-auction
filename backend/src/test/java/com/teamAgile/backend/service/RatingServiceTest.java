package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.model.Rating;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.RatingRepository;
import com.teamAgile.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    private User ratedUser;
    private User raterUser;
    private Rating testRating;
    private UUID ratedUserId;
    private UUID raterUserId;
    private List<Rating> testRatings;

    @BeforeEach
    void setUp() {
        // Set up test users
        ratedUser = new User();
        ratedUserId = UUID.randomUUID();
        ratedUser.setUserID(ratedUserId);
        ratedUser.setUsername("ratedUser");

        raterUser = new User();
        raterUserId = UUID.randomUUID();
        raterUser.setUserID(raterUserId);
        raterUser.setUsername("raterUser");

        // Set up test rating
        testRating = new Rating(ratedUser, raterUser, 4, "Great seller!");

        // Set up test ratings list
        testRatings = new ArrayList<>();
        testRatings.add(testRating);
        testRatings.add(new Rating(ratedUser, raterUser, 5, "Excellent service!"));
        testRatings.add(new Rating(ratedUser, raterUser, 3, "Good but could be better."));
    }

    @Test
    void createRating_Success() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(userRepository.findById(raterUserId)).thenReturn(Optional.of(raterUser));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Rating result = ratingService.createRating(ratedUserId, raterUserId, 4, "Great seller!");

        // Assert
        assertNotNull(result);
        assertEquals(ratedUser, result.getRatedUser());
        assertEquals(raterUser, result.getRaterUser());
        assertEquals(4, result.getRating());
        assertEquals("Great seller!", result.getFeedback());
    }

    @Test
    void createRating_RatedUserNotFound() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> ratingService.createRating(ratedUserId, raterUserId, 4, "Great seller!"));
        assertEquals("Rated user not found", exception.getMessage());
    }

    @Test
    void createRating_RaterUserNotFound() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(userRepository.findById(raterUserId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> ratingService.createRating(ratedUserId, raterUserId, 4, "Great seller!"));
        assertEquals("Rater user not found", exception.getMessage());
    }

    @Test
    void createRating_SelfRating() {
        // Arrange
        UUID sameUserId = UUID.randomUUID();
        User user = new User();
        user.setUserID(sameUserId);

        when(userRepository.findById(sameUserId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> ratingService.createRating(sameUserId, sameUserId, 5, "I'm great!"));
        assertEquals("Cannot rate yourself", exception.getMessage());
    }

    @Test
    void getUserRatings_Success() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        List<Rating> result = ratingService.getUserRatings(ratedUserId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testRatings, result);
    }

    @Test
    void getUserRatings_UserNotFound() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> ratingService.getUserRatings(ratedUserId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserRatingsByUsername_Success() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        List<Rating> result = ratingService.getUserRatingsByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testRatings, result);
    }

    @Test
    void getUserRatingsByUsername_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> ratingService.getUserRatingsByUsername(username));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getAverageUserRating_Success() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        double result = ratingService.getAverageUserRating(ratedUserId);

        // Assert
        assertEquals(4.0, result, 0.001); // Average of 4, 5, and 3
    }

    @Test
    void getAverageUserRating_NoRatings() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(new ArrayList<>());

        // Act
        double result = ratingService.getAverageUserRating(ratedUserId);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getAverageUserRatingByUsername_Success() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        double result = ratingService.getAverageUserRatingByUsername(username);

        // Assert
        assertEquals(4.0, result, 0.001); // Average of 4, 5, and 3
    }

    @Test
    void getAverageUserRatingByUsername_NoRatings() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(new ArrayList<>());

        // Act
        double result = ratingService.getAverageUserRatingByUsername(username);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getTotalUserRatings_Success() {
        // Arrange
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        long result = ratingService.getTotalUserRatings(ratedUserId);

        // Assert
        assertEquals(3, result);
    }

    @Test
    void getTotalUserRatingsByUsername_Success() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        long result = ratingService.getTotalUserRatingsByUsername(username);

        // Assert
        assertEquals(3, result);
    }

    @Test
    void getRatingsByUsername_Success() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        List<Rating> result = ratingService.getRatingsByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testRatings, result);
    }

    @Test
    void getAverageRatingByUsername_Success() {
        // Arrange
        String username = "ratedUser";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.findByRatedUser(ratedUser)).thenReturn(testRatings);

        // Act
        double result = ratingService.getAverageRatingByUsername(username);

        // Assert
        assertEquals(4.0, result, 0.001); // Average of 4, 5, and 3
    }
}