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
        assertNotNull(bid);
        assertEquals(testItemID, bid.getItemID());
        assertEquals(testUser, bid.getUser());
        assertEquals(testBidAmount, bid.getBidAmount());
        assertNotNull(bid.getTimestamp());

        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(testItemID, testUser, -50.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(testItemID, testUser, 0.0);
        });

        assertThrows(NullPointerException.class, () -> {
            new Bid(testItemID, testUser, null);
        });
    }

    @Test
    void testGettersAndSetters() {
        UUID newItemID = UUID.randomUUID();
        bid.setItemID(newItemID);
        assertEquals(newItemID, bid.getItemID());

        assertThrows(IllegalArgumentException.class, () -> {
            bid.setItemID(null);
        });

        User newUser = new User();
        newUser.setUsername("newbidder");
        newUser.setPassword("password456");
        bid.setUser(newUser);
        assertEquals(newUser, bid.getUser());

        Double newBidAmount = 200.0;
        bid.setBidAmount(newBidAmount);
        assertEquals(newBidAmount, bid.getBidAmount());

        LocalDateTime timestamp = bid.getTimestamp();
        assertNotNull(timestamp);
    }

    @Test
    void testBidAmountValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(-100.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            bid.setBidAmount(null);
        });

        bid.setBidAmount(0.01);
        assertEquals(0.01, bid.getBidAmount());

        bid.setBidAmount(999999.99);
        assertEquals(999999.99, bid.getBidAmount());
    }

    @Test
    void testUserRelationship() {
        assertTrue(testUser.getBids().contains(bid));
        assertEquals(testUser, bid.getUser());

        testUser.removeBid(bid);
        assertFalse(testUser.getBids().contains(bid));
        assertNull(bid.getUser());

        testUser.addBid(bid);
        assertTrue(testUser.getBids().contains(bid));
        assertEquals(testUser, bid.getUser());

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
        Bid freshBid = new Bid(UUID.randomUUID(), testUser, 150.0);
        assertNotNull(freshBid.getTimestamp());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(freshBid.getTimestamp().isAfter(now.minusSeconds(10)));
        assertTrue(freshBid.getTimestamp().isBefore(now.plusSeconds(10)));
    }
}