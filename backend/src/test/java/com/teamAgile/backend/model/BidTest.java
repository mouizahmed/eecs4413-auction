package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BidTest {

    private Bid bid;
    private UUID itemId;
    private User user;
    private Double bidAmount;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        user = new User();
        user.setUserID(UUID.randomUUID());
        user.setUsername("testUser");
        bidAmount = 100.0;

        bid = new Bid();
        bid.setItemID(itemId);
        bid.setUser(user);
        bid.setBidAmount(bidAmount);
    }

    @Test
    void testDefaultConstructor() {
        Bid newBid = new Bid();
        assertNotNull(newBid);
    }

    @Test
    void testParameterizedConstructor() {
        Bid newBid = new Bid(itemId, user, bidAmount);

        assertEquals(itemId, newBid.getItemID());
        assertEquals(user, newBid.getUser());
        assertEquals(bidAmount, newBid.getBidAmount());
        assertNull(newBid.getTimestamp()); 
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(itemId, bid.getItemID());
        assertEquals(user, bid.getUser());
        assertEquals(bidAmount, bid.getBidAmount());

  
        UUID newItemId = UUID.randomUUID();
        User newUser = new User();
        newUser.setUserID(UUID.randomUUID());
        newUser.setUsername("newUser");
        Double newBidAmount = 200.0;

        bid.setItemID(newItemId);
        bid.setUser(newUser);
        bid.setBidAmount(newBidAmount);

        assertEquals(newItemId, bid.getItemID());
        assertEquals(newUser, bid.getUser());
        assertEquals(newBidAmount, bid.getBidAmount());
    }

    @Test
    void testBidID() {
        assertNull(bid.getBidID());
    }

    @Test
    void testTimestamp() {
        assertNull(bid.getTimestamp());
    }
}