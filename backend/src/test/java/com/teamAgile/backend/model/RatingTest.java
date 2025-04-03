package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class RatingTest {
    private User ratedUser;
    private User raterUser;
    private Rating rating;

    @BeforeEach
    void setUp() {
        // Create users for testing
        ratedUser = new User();
        ratedUser.setFirstName("John");
        ratedUser.setLastName("Doe");
        ratedUser.setUsername("johndoe");
        ratedUser.setPassword("password123");

        raterUser = new User();
        raterUser.setFirstName("Jane");
        raterUser.setLastName("Smith");
        raterUser.setUsername("janesmith");
        raterUser.setPassword("password456");

        // Create a valid rating
        rating = new Rating(ratedUser, raterUser, 4, "Great seller, item as described!");
    }

    @Test
    void testRatingConstructor() {
        // Test valid rating construction
        assertNotNull(rating);
        assertEquals(ratedUser, rating.getRatedUser());
        assertEquals(raterUser, rating.getRaterUser());
        assertEquals(4, rating.getRating());
        assertEquals("Great seller, item as described!", rating.getFeedback());
        assertNotNull(rating.getTimestamp());

        // Test invalid rating values
        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 0, "Too low rating"); // Below minimum
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 6, "Too high rating"); // Above maximum
        });

        // Test null/empty feedback
        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, null); // Null feedback
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, ""); // Empty feedback
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, "   "); // Whitespace feedback
        });
    }

    @Test
    void testGetAndSetRatedUser() {
        // Test normal case
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password789");

        rating.setRatedUser(newUser);
        assertEquals(newUser, rating.getRatedUser());

        // While not explicitly checked in the model, test null for completeness
        rating.setRatedUser(null); // Should not throw exception as no validation in setter
        assertNull(rating.getRatedUser());
    }

    @Test
    void testGetAndSetRaterUser() {
        // Test normal case
        User newUser = new User();
        newUser.setUsername("newrater");
        newUser.setPassword("password789");

        rating.setRaterUser(newUser);
        assertEquals(newUser, rating.getRaterUser());

        // While not explicitly checked in the model, test null for completeness
        rating.setRaterUser(null); // Should not throw exception as no validation in setter
        assertNull(rating.getRaterUser());
    }

    @Test
    void testGetAndSetRating() {
        // Test valid values
        rating.setRating(1); // Minimum
        assertEquals(1, rating.getRating());

        rating.setRating(5); // Maximum
        assertEquals(5, rating.getRating());

        rating.setRating(3); // Middle value
        assertEquals(3, rating.getRating());

        // Test invalid values
        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(0); // Below minimum
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(6); // Above maximum
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(-1); // Negative
        });
    }

    @Test
    void testGetAndSetFeedback() {
        // Test valid feedback
        rating.setFeedback("New feedback text");
        assertEquals("New feedback text", rating.getFeedback());

        // Test boundary (long but valid feedback)
        String longFeedback = "a".repeat(500); // 500 characters is the max length per Rating.java
        rating.setFeedback(longFeedback);
        assertEquals(longFeedback, rating.getFeedback());

        // Test invalid feedback
        assertThrows(IllegalArgumentException.class, () -> {
            rating.setFeedback(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setFeedback("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setFeedback("   ");
        });
    }

    @Test
    void testGetAndSetTimestamp() {
        // Test timestamp setting
        LocalDateTime newTimestamp = LocalDateTime.now().minusDays(1);
        rating.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, rating.getTimestamp());

        // Test current timestamp from constructor
        Rating freshRating = new Rating(ratedUser, raterUser, 5, "Excellent!");
        assertNotNull(freshRating.getTimestamp());

        // The timestamp should be close to now (within a few seconds)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(freshRating.getTimestamp().isAfter(now.minusSeconds(10)));
        assertTrue(freshRating.getTimestamp().isBefore(now.plusSeconds(10)));

        // Test null timestamp (while not validated in the model, test for completeness)
        rating.setTimestamp(null);
        assertNull(rating.getTimestamp());
    }

    @Test
    void testGetRatingID() {
        // ID is set by JPA/database, so we can only verify that it's null before
        // persistence
        assertNull(rating.getRatingID());
    }
}