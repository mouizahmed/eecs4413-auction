package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForwardAuctionItemTest {

	private ForwardAuctionItem auctionItem;
	private String itemName;
	private User seller;
	private AuctionItem.AuctionStatus auctionStatus;
	private Double currentPrice;
	private Integer shippingTime;
	private LocalDateTime endTime;

	@BeforeEach
	void setUp() throws Exception {
		itemName = "Test Item";
		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");
		auctionStatus = AuctionItem.AuctionStatus.AVAILABLE;
		currentPrice = 100.0;
		shippingTime = 5;
		endTime = LocalDateTime.now().plusDays(7);

		auctionItem = new ForwardAuctionItem();
		auctionItem.setItemName(itemName);
		auctionItem.setSeller(seller);
		auctionItem.setAuctionStatus(auctionStatus);
		auctionItem.setCurrentPrice(currentPrice);
		auctionItem.setShippingTime(shippingTime);
		auctionItem.setEndTime(endTime);

		java.lang.reflect.Field field = AuctionItem.class.getDeclaredField("auctionType");
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
		ForwardAuctionItem newItem = new ForwardAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime,
				endTime);

		assertEquals(itemName, newItem.getItemName());
		assertEquals(seller, newItem.getSeller());
		assertEquals(auctionStatus, newItem.getAuctionStatus());
		assertEquals(currentPrice, newItem.getCurrentPrice());
		assertEquals(shippingTime, newItem.getShippingTime());
		assertEquals(endTime, newItem.getEndTime());
	}

	@Test
	void testGettersAndSetters() {
		assertEquals(itemName, auctionItem.getItemName());
		assertEquals(seller, auctionItem.getSeller());
		assertEquals(auctionStatus, auctionItem.getAuctionStatus());
		assertEquals(currentPrice, auctionItem.getCurrentPrice());
		assertEquals(shippingTime, auctionItem.getShippingTime());
		assertEquals(endTime, auctionItem.getEndTime());

		String newItemName = "Updated Item";
		User newSeller = new User();
		newSeller.setUserID(UUID.randomUUID());
		newSeller.setUsername("newSeller");
		AuctionItem.AuctionStatus newAuctionStatus = AuctionItem.AuctionStatus.SOLD;
		Double newCurrentPrice = 200.0;
		Integer newShippingTime = 10;
		LocalDateTime newEndTime = LocalDateTime.now().plusDays(10);

		auctionItem.setItemName(newItemName);
		auctionItem.setSeller(newSeller);
		auctionItem.setAuctionStatus(newAuctionStatus);
		auctionItem.setCurrentPrice(newCurrentPrice);
		auctionItem.setShippingTime(newShippingTime);
		auctionItem.setEndTime(newEndTime);

		assertEquals(newItemName, auctionItem.getItemName());
		assertEquals(newSeller, auctionItem.getSeller());
		assertEquals(newAuctionStatus, auctionItem.getAuctionStatus());
		assertEquals(newCurrentPrice, auctionItem.getCurrentPrice());
		assertEquals(newShippingTime, auctionItem.getShippingTime());
		assertEquals(newEndTime, auctionItem.getEndTime());
	}

	@Test
	void testEndTimeValidation() {

		LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.setEndTime(pastTime);
		});

		assertEquals("End time must be in the future.", exception.getMessage());

		exception = assertThrows(IllegalArgumentException.class, () -> {
			new ForwardAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, pastTime);
		});

		assertEquals("End time must be in the future.", exception.getMessage());
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

	@Test
	void testMakePaymentWithWrongStatus() {

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		auctionItem.setHighestBidder(bidder);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE); // Wrong status

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			auctionItem.makePayment(bidder);
		});

		assertEquals("The auction is either over or still ongoing.", exception.getMessage());
	}
}