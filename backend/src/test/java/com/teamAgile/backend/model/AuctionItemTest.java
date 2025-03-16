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
}