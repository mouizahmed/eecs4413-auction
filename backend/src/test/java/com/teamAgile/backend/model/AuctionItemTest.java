package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuctionItemTest {

	private ForwardAuctionItem auctionItem;
	private UUID itemId;
	private String name;
	private String description;
	private double startingPrice;
	private double currentPrice;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private User seller;
	private String imageUrl;
	private String category;
	private boolean isActive;

	@BeforeEach
	void setUp() throws Exception {
		itemId = UUID.randomUUID();
		name = "Test Item";
		description = "This is a test item";
		startingPrice = 100.0;
		currentPrice = 100.0;
		startTime = LocalDateTime.now();
		endTime = LocalDateTime.now().plusDays(7);
		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");
		imageUrl = "http://example.com/image.jpg";
		category = "Electronics";
		isActive = true;

		auctionItem = new ForwardAuctionItem();
		auctionItem.setItemName(name);
		auctionItem.setCurrentPrice(currentPrice);
		auctionItem.setSeller(seller);
		auctionItem.setEndTime(endTime);
		auctionItem.setShippingTime(5);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		Field field = AuctionItem.class.getDeclaredField("auctionType");
		field.setAccessible(true);
		field.set(auctionItem, AuctionItem.AuctionType.FORWARD);
	}

	@Test
	void testDefaultConstructor() {
		ForwardAuctionItem newItem = new ForwardAuctionItem();
		assertNotNull(newItem);
	}

	@Test
	void testParameterizedConstructor() {
		ForwardAuctionItem newItem = new ForwardAuctionItem(name, seller, AuctionItem.AuctionStatus.AVAILABLE,
				currentPrice, 5, endTime);

		assertEquals(name, newItem.getItemName());
		assertEquals(seller, newItem.getSeller());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, newItem.getAuctionStatus());
		assertEquals(currentPrice, newItem.getCurrentPrice());
		assertEquals(5, newItem.getShippingTime());
		assertEquals(endTime, newItem.getEndTime());
		assertEquals(AuctionItem.AuctionType.FORWARD, newItem.getAuctionType());
	}

	@Test
	void testEndTimeValidation() {
		LocalDateTime pastEndTime = LocalDateTime.now().minusDays(1);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.setEndTime(pastEndTime);
		});

		assertEquals("End time must be in the future.", exception.getMessage());

		exception = assertThrows(IllegalArgumentException.class, () -> {
			new ForwardAuctionItem(name, seller, AuctionItem.AuctionStatus.AVAILABLE, currentPrice, 5, pastEndTime);
		});

		assertEquals("End time must be in the future.", exception.getMessage());
	}

	@Test
	void testGettersAndSetters() {
		assertEquals(name, auctionItem.getItemName());
		assertEquals(currentPrice, auctionItem.getCurrentPrice());
		assertEquals(seller, auctionItem.getSeller());
		assertEquals(endTime, auctionItem.getEndTime());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, auctionItem.getAuctionStatus());
		assertEquals(AuctionItem.AuctionType.FORWARD, auctionItem.getAuctionType());

		String newName = "Updated Item";
		double newCurrentPrice = 200.0;
		User newSeller = new User();
		newSeller.setUserID(UUID.randomUUID());
		newSeller.setUsername("newSeller");
		LocalDateTime newEndTime = LocalDateTime.now().plusDays(10);

		auctionItem.setItemName(newName);
		auctionItem.setCurrentPrice(newCurrentPrice);
		auctionItem.setSeller(newSeller);
		auctionItem.setEndTime(newEndTime);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		assertEquals(newName, auctionItem.getItemName());
		assertEquals(newCurrentPrice, auctionItem.getCurrentPrice());
		assertEquals(newSeller, auctionItem.getSeller());
		assertEquals(newEndTime, auctionItem.getEndTime());
		assertEquals(AuctionItem.AuctionStatus.SOLD, auctionItem.getAuctionStatus());
	}

	@Test
	void testPlaceBid() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = 150.0;
		auctionItem.placeBid(bidAmount, bidder);

		assertEquals(bidAmount, auctionItem.getCurrentPrice());
		assertEquals(bidder, auctionItem.getHighestBidder());
	}

	@Test
	void testPlaceBidLowerThanCurrentPrice() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = 50.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.placeBid(bidAmount, bidder);
		});

		assertEquals("Bid price must be greater than the current price.", exception.getMessage());
		assertEquals(currentPrice, auctionItem.getCurrentPrice()); // Price should remain unchanged
		assertNull(auctionItem.getHighestBidder()); // No bidder should be set
	}

	@Test
	void testPlaceBidAfterAuctionEnded() throws Exception {

		LocalDateTime pastEndTime = LocalDateTime.now().minusDays(1);

		Field endTimeField = ForwardAuctionItem.class.getDeclaredField("endTime");
		endTimeField.setAccessible(true);
		endTimeField.set(auctionItem, pastEndTime);

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = 150.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.placeBid(bidAmount, bidder);
		});

		assertEquals("This forward auction has now closed.", exception.getMessage());
	}

	@Test
	void testPlaceBidWhenAuctionNotAvailable() {
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = 150.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.placeBid(bidAmount, bidder);
		});

		assertEquals("Auction Item is currently not available.", exception.getMessage());
	}

	@Test
	void testMakePayment() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		auctionItem.setHighestBidder(bidder);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		auctionItem.makePayment(bidder);

		assertEquals(AuctionItem.AuctionStatus.PAID, auctionItem.getAuctionStatus());
	}

	@Test
	void testMakePaymentWithWrongUser() {
		User winningBidder = new User();
		winningBidder.setUserID(UUID.randomUUID());
		winningBidder.setUsername("winningBidder");

		User wrongBidder = new User();
		wrongBidder.setUserID(UUID.randomUUID());
		wrongBidder.setUsername("wrongBidder");

		auctionItem.setHighestBidder(winningBidder);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.makePayment(wrongBidder);
		});

		assertEquals("You must be the winning bidder to place a payment on this item.", exception.getMessage());
	}

	@Test
	void testMakePaymentWhenAuctionNotSold() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		auctionItem.setHighestBidder(bidder);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE); // Not SOLD status

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.makePayment(bidder);
		});

		assertEquals("The auction is either over or still ongoing.", exception.getMessage());
	}

	@Test
	void testAuctionStatusTransitions() {

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, auctionItem.getAuctionStatus());

		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		assertEquals(AuctionItem.AuctionStatus.SOLD, auctionItem.getAuctionStatus());

		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.PAID);
		assertEquals(AuctionItem.AuctionStatus.PAID, auctionItem.getAuctionStatus());

		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		// Transition to EXPIRED
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.EXPIRED);
		assertEquals(AuctionItem.AuctionStatus.EXPIRED, auctionItem.getAuctionStatus());

		// Transition to CANCELLED
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.CANCELLED);
		assertEquals(AuctionItem.AuctionStatus.CANCELLED, auctionItem.getAuctionStatus());
	}

	@Test
	void testBidHistory() {

		assertTrue(auctionItem.getBids().isEmpty());

		User bidder1 = new User();
		bidder1.setUserID(UUID.randomUUID());
		bidder1.setUsername("bidder1");

		User bidder2 = new User();
		bidder2.setUserID(UUID.randomUUID());
		bidder2.setUsername("bidder2");

		Bid bid1 = new Bid();
		bid1.setUser(bidder1);
		bid1.setBidAmount(110.0);

		Bid bid2 = new Bid();
		bid2.setUser(bidder2);
		bid2.setBidAmount(120.0);

		auctionItem.getBids().add(bid1);
		auctionItem.getBids().add(bid2);

		assertEquals(2, auctionItem.getBids().size());
		assertTrue(auctionItem.getBids().contains(bid1));
		assertTrue(auctionItem.getBids().contains(bid2));
	}
}