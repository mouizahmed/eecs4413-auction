package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class BidTest {
    private Bid bid;
    private UUID testItemID;
    private User testUser;
    private Double testBidAmount;

    @BeforeEach
    void setUp() {
        testItemID = UUID.randomUUID();
        testUser = new User();
        testBidAmount = 100.0;
        bid = new Bid(testItemID, testUser, testBidAmount);
    }

    @Test
    void testGettersAndSetters() {
        // Test itemID
        UUID newItemID = UUID.randomUUID();
        bid.setItemID(newItemID);
        assertEquals(newItemID, bid.getItemID());

        // Test user
        User newUser = new User();
        bid.setUser(newUser);
        assertEquals(newUser, bid.getUser());

        // Test bidAmount
        Double newBidAmount = 200.0;
        bid.setBidAmount(newBidAmount);
        assertEquals(newBidAmount, bid.getBidAmount());
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
    }
}