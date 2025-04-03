package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.time.LocalDateTime;

class BidTest {
    private Bid bid;
    private UUID testItemID;
    private User testUser;
    private Double testBidAmount;

    @BeforeEach
    void setUp() {
        testItemID = UUID.randomUUID();
        testUser = new User();
        testUser.setUsername("bidder");
        testUser.setPassword("password123");
        testBidAmount = 100.0;
        bid = new Bid(testItemID, testUser, testBidAmount);
    }

    @Test
    void testConstructor() {
        // Test valid construction
        assertNotNull(bid);
        assertEquals(testItemID, bid.getItemID());
        assertEquals(testUser, bid.getUser());
        assertEquals(testBidAmount, bid.getBidAmount());
        assertNotNull(bid.getTimestamp());

        // Test constructor with invalid bid amount
        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(testItemID, testUser, -50.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(testItemID, testUser, 0.0);
        });

        // Test null bidAmount - throws NullPointerException not
        // IllegalArgumentException
        assertThrows(NullPointerException.class, () -> {
            new Bid(testItemID, testUser, null);
        });

        // Note: The constructor doesn't validate null itemID or null user
        // so we don't test those cases
    }

    @Test
    void testGettersAndSetters() {
        // Test itemID
        UUID newItemID = UUID.randomUUID();
        bid.setItemID(newItemID);
        assertEquals(newItemID, bid.getItemID());

        // Test null itemID - this is validated
        assertThrows(IllegalArgumentException.class, () -> {
            bid.setItemID(null);
        });

        // Test user
        User newUser = new User();
        newUser.setUsername("newbidder");
        newUser.setPassword("password456");
        bid.setUser(newUser);
        assertEquals(newUser, bid.getUser());

        // setUser() doesn't validate against null, so we don't test that exception

        // Test bidAmount
        Double newBidAmount = 200.0;
        bid.setBidAmount(newBidAmount);
        assertEquals(newBidAmount, bid.getBidAmount());

        // Test timestamp is read-only
        LocalDateTime timestamp = bid.getTimestamp();
        assertNotNull(timestamp);
    }

    @Test
    void testBidAmountValidation() {
        // Test setting a negative bid amount
        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(-100.0);
        });

        // Test setting a zero bid amount
        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(0.0);
        });

        // Test setting a null bid amount
        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(null);
        });

        // Test valid bid amounts
        bid.setBidAmount(0.01); // Minimum possible value
        assertEquals(0.01, bid.getBidAmount());

        bid.setBidAmount(999999.99); // High value
        assertEquals(999999.99, bid.getBidAmount());
    }

    @Test
    void testUserRelationship() {
        // Test bidirectional relationship
        assertTrue(testUser.getBids().contains(bid));
        assertEquals(testUser, bid.getUser());

        // Test removing bid from user
        testUser.removeBid(bid);
        assertFalse(testUser.getBids().contains(bid));
        assertNull(bid.getUser());

        // Test adding bid back to user
        testUser.addBid(bid);
        assertTrue(testUser.getBids().contains(bid));
        assertEquals(testUser, bid.getUser());

        // Test changing user
        User newUser = new User();
        newUser.setUsername("anotherbidder");
        newUser.setPassword("password789");

        bid.setUser(newUser);
        assertFalse(testUser.getBids().contains(bid));
        assertTrue(newUser.getBids().contains(bid));
        assertEquals(newUser, bid.getUser());
    }

    @Test
    void testTimestampBehavior() {
        // Test timestamp is automatically set during construction
        Bid freshBid = new Bid(UUID.randomUUID(), testUser, 150.0);
        assertNotNull(freshBid.getTimestamp());

        // The timestamp should be close to now (within a few seconds)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(freshBid.getTimestamp().isAfter(now.minusSeconds(10)));
        assertTrue(freshBid.getTimestamp().isBefore(now.plusSeconds(10)));

        // Timestamp is read-only after creation (no setter method available)
    }

    @Test
    void testGetBidID() {
        // ID is set by JPA/database, so we can only verify that it's null before
        // persistence
        assertNull(bid.getBidID());
    }
}