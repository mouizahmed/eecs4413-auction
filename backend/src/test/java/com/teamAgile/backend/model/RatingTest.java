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

        rating = new Rating(ratedUser, raterUser, 4, "Great seller, item as described!");
    }

    @Test
    void testRatingConstructor() {
        assertNotNull(rating);
        assertEquals(ratedUser, rating.getRatedUser());
        assertEquals(raterUser, rating.getRaterUser());
        assertEquals(4, rating.getRating());
        assertEquals("Great seller, item as described!", rating.getFeedback());
        assertNotNull(rating.getTimestamp());

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 0, "Too low rating");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 6, "Too high rating");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Rating(ratedUser, raterUser, 3, "   ");
        });
    }

    @Test
    void testGetAndSetRatedUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password789");

        rating.setRatedUser(newUser);
        assertEquals(newUser, rating.getRatedUser());

        rating.setRatedUser(null);
        assertNull(rating.getRatedUser());
    }

    @Test
    void testGetAndSetRaterUser() {
        User newUser = new User();
        newUser.setUsername("newrater");
        newUser.setPassword("password789");

        rating.setRaterUser(newUser);
        assertEquals(newUser, rating.getRaterUser());

        rating.setRaterUser(null);
        assertNull(rating.getRaterUser());
    }

    @Test
    void testGetAndSetRating() {
        rating.setRating(1);
        assertEquals(1, rating.getRating());

        rating.setRating(5);
        assertEquals(5, rating.getRating());

        rating.setRating(3);
        assertEquals(3, rating.getRating());

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(6);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRating(-1);
        });
    }

    @Test
    void testGetAndSetFeedback() {
        rating.setFeedback("New feedback text");
        assertEquals("New feedback text", rating.getFeedback());

        String longFeedback = "a".repeat(500);
        rating.setFeedback(longFeedback);
        assertEquals(longFeedback, rating.getFeedback());

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
        LocalDateTime newTimestamp = LocalDateTime.now().minusDays(1);
        rating.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, rating.getTimestamp());

        Rating freshRating = new Rating(ratedUser, raterUser, 5, "Excellent!");
        assertNotNull(freshRating.getTimestamp());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(freshRating.getTimestamp().isAfter(now.minusSeconds(10)));
        assertTrue(freshRating.getTimestamp().isBefore(now.plusSeconds(10)));

        rating.setTimestamp(null);
        assertNull(rating.getTimestamp());
    }
}