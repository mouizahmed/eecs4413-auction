package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class DutchAuctionItemTest {
    private DutchAuctionItem auctionItem;
    private User testSeller;
    private String testItemName;
    private Double testCurrentPrice;
    private Integer testShippingTime;
    private Double testReservePrice;

    @BeforeEach
    void setUp() {
        testSeller = new User(
                UUID.randomUUID(),
                "Test",
                "Seller",
                "testseller",
                "Test Street",
                123,
                "A1A 1A1",
                "Test City",
                "Test Province",
                "Test Country");
        testItemName = "Test Item";
        testCurrentPrice = 100.0;
        testShippingTime = 5;
        testReservePrice = 50.0;
        auctionItem = new DutchAuctionItem(testItemName, testSeller, AuctionItem.AuctionStatus.AVAILABLE,
                testCurrentPrice, testShippingTime, testReservePrice);
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

        // Test reservePrice
        Double newReservePrice = 100.0;
        auctionItem.setReservePrice(newReservePrice);
        assertEquals(newReservePrice, auctionItem.getReservePrice());

        // Test seller
        User newSeller = new User(
                UUID.randomUUID(),
                "New",
                "Seller",
                "newseller",
                "New Street",
                456,
                "B2B 2B2",
                "New City",
                "New Province",
                "New Country");
        auctionItem.setSeller(newSeller);
        assertEquals(newSeller, auctionItem.getSeller());

        // Test highestBidder
        User newHighestBidder = new User(
                UUID.randomUUID(),
                "New",
                "Bidder",
                "newbidder",
                "Bidder Street",
                789,
                "C3C 3C3",
                "Bidder City",
                "Bidder Province",
                "Bidder Country");
        auctionItem.setHighestBidder(newHighestBidder);
        assertEquals(newHighestBidder, auctionItem.getHighestBidder());

        // Test auctionStatus
        auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
        assertEquals(AuctionItem.AuctionStatus.SOLD, auctionItem.getAuctionStatus());
    }

    @Test
    void testReservePriceValidation() {
        // Test setting reserve price equal to current price
        assertThrows(IllegalArgumentException.class, () -> {
            auctionItem.setReservePrice(testCurrentPrice);
        });

        // Test setting reserve price higher than current price
        assertThrows(IllegalArgumentException.class, () -> {
            auctionItem.setReservePrice(testCurrentPrice + 10.0);
        });

        // Test creating with invalid reserve price
        assertThrows(IllegalArgumentException.class, () -> {
            new DutchAuctionItem(testItemName, testSeller, AuctionItem.AuctionStatus.AVAILABLE,
                    testCurrentPrice, testShippingTime, testCurrentPrice);
        });
    }

    @Test
    void testDecreasePrice() {
        // Test normal price decrease
        auctionItem.decreasePrice(10.0);
        assertEquals(90.0, auctionItem.getCurrentPrice());
        assertEquals(AuctionItem.AuctionStatus.AVAILABLE, auctionItem.getAuctionStatus());

        // Test price decrease to reserve price
        auctionItem.decreasePrice(40.0);
        assertEquals(50.0, auctionItem.getCurrentPrice());
        assertEquals(AuctionItem.AuctionStatus.EXPIRED, auctionItem.getAuctionStatus());

        // Test price decrease below reserve price
        auctionItem.setCurrentPrice(100.0);
        auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
        auctionItem.decreasePrice(60.0);
        assertEquals(50.0, auctionItem.getCurrentPrice());
        assertEquals(AuctionItem.AuctionStatus.EXPIRED, auctionItem.getAuctionStatus());
    }

    @Test
    void testBidManagement() {
        User bidder = new User(
                UUID.randomUUID(),
                "Test",
                "Bidder",
                "testbidder",
                "Bidder Street",
                123,
                "A1A 1A1",
                "Bidder City",
                "Bidder Province",
                "Bidder Country");
        Double bidAmount = 100.0; // Must equal current price for Dutch auction

        // Test placing a bid
        auctionItem.placeBid(bidAmount, bidder);
        assertEquals(bidAmount, auctionItem.getCurrentPrice());
        assertEquals(bidder, auctionItem.getHighestBidder());
        assertTrue(auctionItem.getBids().size() > 0);
    }

    @Test
    void testPaymentManagement() {
        // Test successful payment
        User winningBidder = new User(
                UUID.randomUUID(),
                "Winning",
                "Bidder",
                "winningbidder",
                "Winning Street",
                123,
                "A1A 1A1",
                "Winning City",
                "Winning Province",
                "Winning Country");
        DutchAuctionItem soldItem = new DutchAuctionItem(testItemName, testSeller,
                AuctionItem.AuctionStatus.SOLD, testCurrentPrice, testShippingTime, testReservePrice);
        soldItem.setHighestBidder(winningBidder);
        soldItem.makePayment(winningBidder);
        assertEquals(AuctionItem.AuctionStatus.PAID, soldItem.getAuctionStatus());

        // Test payment by non-winning bidder
        User otherUser = new User(
                UUID.randomUUID(),
                "Other",
                "User",
                "otheruser",
                "Other Street",
                456,
                "B2B 2B2",
                "Other City",
                "Other Province",
                "Other Country");
        DutchAuctionItem anotherSoldItem = new DutchAuctionItem(testItemName + "2", testSeller,
                AuctionItem.AuctionStatus.SOLD, testCurrentPrice, testShippingTime, testReservePrice);
        anotherSoldItem.setHighestBidder(winningBidder);
        assertThrows(IllegalArgumentException.class, () -> {
            anotherSoldItem.makePayment(otherUser);
        });

        // Test payment on non-sold item
        DutchAuctionItem availableItem = new DutchAuctionItem(testItemName + "3", testSeller,
                AuctionItem.AuctionStatus.AVAILABLE, testCurrentPrice, testShippingTime, testReservePrice);
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