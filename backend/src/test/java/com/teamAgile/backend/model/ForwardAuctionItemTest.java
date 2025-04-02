package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

class ForwardAuctionItemTest {
    private ForwardAuctionItem auctionItem;
    private User testSeller;
    private String testItemName;
    private Double testCurrentPrice;
    private Integer testShippingTime;
    private LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setUserID(UUID.randomUUID());
        testItemName = "Test Item";
        testCurrentPrice = 100.0;
        testShippingTime = 5;
        testEndTime = LocalDateTime.now().plusDays(7); // 7 days from now
        auctionItem = new ForwardAuctionItem(testItemName, testSeller, AuctionItem.AuctionStatus.AVAILABLE,
                testCurrentPrice, testShippingTime, testEndTime);
        testSeller.addAuctionItem(auctionItem);
    }

    @Test
    void testGettersAndSetters() {
        // Test itemName
        String newItemName = "New Item Name";
        auctionItem.setItemName(newItemName);
        assertEquals(newItemName, auctionItem.getItemName());

        // Test currentPrice
        Double newPrice = 200.0;
        auctionItem.setCurrentPrice(newPrice);
        assertEquals(newPrice, auctionItem.getCurrentPrice());

        // Test shippingTime
        Integer newShippingTime = 10;
        auctionItem.setShippingTime(newShippingTime);
        assertEquals(newShippingTime, auctionItem.getShippingTime());

        // Test endTime
        LocalDateTime newEndTime = LocalDateTime.now().plusDays(14);
        auctionItem.setEndTime(newEndTime);
        assertEquals(newEndTime, auctionItem.getEndTime());

        // Test seller
        User newSeller = new User();
        auctionItem.setSeller(newSeller);
        assertEquals(newSeller, auctionItem.getSeller());

        // Test highestBidder
        User newHighestBidder = new User();
        auctionItem.setHighestBidder(newHighestBidder);
        assertEquals(newHighestBidder, auctionItem.getHighestBidder());

        // Test auctionStatus
        auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
        assertEquals(AuctionItem.AuctionStatus.SOLD, auctionItem.getAuctionStatus());
    }

    @Test
    void testEndTimeValidation() {
        // Test setting end time in the past
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> {
            auctionItem.setEndTime(pastTime);
        });

        // Test creating with end time in the past
        assertThrows(IllegalArgumentException.class, () -> {
            new ForwardAuctionItem(testItemName, testSeller, AuctionItem.AuctionStatus.AVAILABLE,
                    testCurrentPrice, testShippingTime, pastTime);
        });
    }

    @Test
    void testBidManagement() {
        User bidder = new User();
        bidder.setUserID(UUID.randomUUID());
        Double bidAmount = 150.0;

        // Test placing a bid
        auctionItem.placeBid(bidAmount, bidder);
        assertEquals(bidAmount, auctionItem.getCurrentPrice());
        assertEquals(bidder, auctionItem.getHighestBidder());
        assertTrue(auctionItem.getBids().size() > 0);
    }

    @Test
    void testPaymentManagement() {
        // Test successful payment
        User winningBidder = new User();
        winningBidder.setUserID(UUID.randomUUID());
        ForwardAuctionItem soldItem = new ForwardAuctionItem(testItemName, testSeller,
                AuctionItem.AuctionStatus.SOLD, testCurrentPrice, testShippingTime, testEndTime);
        soldItem.setHighestBidder(winningBidder);
        soldItem.makePayment(winningBidder);
        assertEquals(AuctionItem.AuctionStatus.PAID, soldItem.getAuctionStatus());

        // Test payment by non-winning bidder
        User otherUser = new User();
        otherUser.setUserID(UUID.randomUUID());
        ForwardAuctionItem anotherSoldItem = new ForwardAuctionItem(testItemName + "2", testSeller,
                AuctionItem.AuctionStatus.SOLD, testCurrentPrice, testShippingTime, testEndTime);
        anotherSoldItem.setHighestBidder(winningBidder);
        assertThrows(IllegalArgumentException.class, () -> {
            anotherSoldItem.makePayment(otherUser);
        });

        // Test payment on non-sold item
        ForwardAuctionItem availableItem = new ForwardAuctionItem(testItemName + "3", testSeller,
                AuctionItem.AuctionStatus.AVAILABLE, testCurrentPrice, testShippingTime, testEndTime);
        availableItem.setHighestBidder(winningBidder);
        assertThrows(IllegalArgumentException.class, () -> {
            availableItem.makePayment(winningBidder);
        });
    }

    @Test
    void testSellerRelationship() {
        // Test bidirectional relationship
        assertTrue(testSeller.getAuctionItems().contains(auctionItem));
        assertEquals(testSeller, auctionItem.getSeller());

        // Test removing item from seller
        testSeller.removeAuctionItem(auctionItem);
        assertFalse(testSeller.getAuctionItems().contains(auctionItem));
        assertNull(auctionItem.getSeller());
    }
}