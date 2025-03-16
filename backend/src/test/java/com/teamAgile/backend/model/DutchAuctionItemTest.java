package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DutchAuctionItemTest {

	private DutchAuctionItem dutchAuctionItem;
	private String itemName;
	private User seller;
	private AuctionItem.AuctionStatus auctionStatus;
	private Double currentPrice;
	private Integer shippingTime;
	private Double reservePrice;

	@BeforeEach
	void setUp() throws Exception {
		itemName = "Test Dutch Auction Item";
		currentPrice = 200.0;
		reservePrice = 100.0;
		shippingTime = 5;
		auctionStatus = AuctionItem.AuctionStatus.AVAILABLE;

		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");

		dutchAuctionItem = new DutchAuctionItem();
		dutchAuctionItem.setItemName(itemName);
		dutchAuctionItem.setCurrentPrice(currentPrice);
		dutchAuctionItem.setReservePrice(reservePrice);
		dutchAuctionItem.setSeller(seller);
		dutchAuctionItem.setShippingTime(shippingTime);
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		java.lang.reflect.Field field = AuctionItem.class.getDeclaredField("auctionType");
		field.setAccessible(true);
		field.set(dutchAuctionItem, AuctionItem.AuctionType.DUTCH);
	}

	@Test
	void testDefaultConstructor() {
		DutchAuctionItem newItem = new DutchAuctionItem();
		assertNotNull(newItem);
	}

	@Test
	void testParameterizedConstructor() {
		DutchAuctionItem newItem = new DutchAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime,
				reservePrice);

		assertEquals(itemName, newItem.getItemName());
		assertEquals(seller, newItem.getSeller());
		assertEquals(auctionStatus, newItem.getAuctionStatus());
		assertEquals(currentPrice, newItem.getCurrentPrice());
		assertEquals(shippingTime, newItem.getShippingTime());
		assertEquals(reservePrice, newItem.getReservePrice());
		assertEquals(AuctionItem.AuctionType.DUTCH, newItem.getAuctionType());
	}

	@Test
	void testGettersAndSetters() {

		assertEquals(itemName, dutchAuctionItem.getItemName());
		assertEquals(currentPrice, dutchAuctionItem.getCurrentPrice());
		assertEquals(reservePrice, dutchAuctionItem.getReservePrice());
		assertEquals(seller, dutchAuctionItem.getSeller());
		assertEquals(shippingTime, dutchAuctionItem.getShippingTime());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());
		assertEquals(AuctionItem.AuctionType.DUTCH, dutchAuctionItem.getAuctionType());

		String newItemName = "Updated Item";
		User newSeller = new User();
		newSeller.setUserID(UUID.randomUUID());
		newSeller.setUsername("newSeller");
		AuctionItem.AuctionStatus newAuctionStatus = AuctionItem.AuctionStatus.SOLD;
		Double newCurrentPrice = 200.0;
		Integer newShippingTime = 10;
		Double newReservePrice = 75.0;

		dutchAuctionItem.setItemName(newItemName);
		dutchAuctionItem.setSeller(newSeller);
		dutchAuctionItem.setAuctionStatus(newAuctionStatus);
		dutchAuctionItem.setCurrentPrice(newCurrentPrice);
		dutchAuctionItem.setShippingTime(newShippingTime);
		dutchAuctionItem.setReservePrice(newReservePrice);

		assertEquals(newItemName, dutchAuctionItem.getItemName());
		assertEquals(newSeller, dutchAuctionItem.getSeller());
		assertEquals(newAuctionStatus, dutchAuctionItem.getAuctionStatus());
		assertEquals(newCurrentPrice, dutchAuctionItem.getCurrentPrice());
		assertEquals(newShippingTime, dutchAuctionItem.getShippingTime());
		assertEquals(newReservePrice, dutchAuctionItem.getReservePrice());
	}

	@Test
	void testReservePriceValidation() {

		Double invalidReservePrice = currentPrice + 10.0;
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.setReservePrice(invalidReservePrice);
		});

		assertEquals("Reserve price must be below the current price.", exception.getMessage());

		exception = assertThrows(IllegalArgumentException.class, () -> {
			new DutchAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, invalidReservePrice);
		});

		assertEquals("Reserve price must be below the current price.", exception.getMessage());
	}

	@Test
	void testDecreasePrice() {

		double decreaseAmount = 20.0;
		double expectedNewPrice = currentPrice - decreaseAmount;

		dutchAuctionItem.decreasePrice(decreaseAmount);

		assertEquals(expectedNewPrice, dutchAuctionItem.getCurrentPrice());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());

		double largeDecreaseAmount = 100.0;

		dutchAuctionItem.decreasePrice(largeDecreaseAmount);

		assertEquals(reservePrice, dutchAuctionItem.getCurrentPrice());
		assertEquals(AuctionItem.AuctionStatus.EXPIRED, dutchAuctionItem.getAuctionStatus());
	}

	@Test
	void testPlaceBid() {

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = currentPrice;
		dutchAuctionItem.placeBid(bidAmount, bidder);

		assertEquals(bidAmount, dutchAuctionItem.getCurrentPrice());
		assertEquals(bidder, dutchAuctionItem.getHighestBidder());
	}

	@Test
	void testPlaceBidWithIncorrectAmount() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = currentPrice - 10.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.placeBid(bidAmount, bidder);
		});

		assertEquals("For Dutch auctions, bid amount must equal the current price.", exception.getMessage());
		assertNull(dutchAuctionItem.getHighestBidder());
	}

	@Test
	void testPlaceBidWhenAuctionNotAvailable() {
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		Double bidAmount = currentPrice;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.placeBid(bidAmount, bidder);
		});

		assertEquals("Auction Item is currently not available.", exception.getMessage());
	}

	@Test
	void testMakePayment() {

		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		dutchAuctionItem.setHighestBidder(bidder);
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		dutchAuctionItem.makePayment(bidder);

		assertEquals(AuctionItem.AuctionStatus.PAID, dutchAuctionItem.getAuctionStatus());
	}

	@Test
	void testMakePaymentWithWrongUser() {
		User winningBidder = new User();
		winningBidder.setUserID(UUID.randomUUID());
		winningBidder.setUsername("winningBidder");

		User wrongBidder = new User();
		wrongBidder.setUserID(UUID.randomUUID());
		wrongBidder.setUsername("wrongBidder");

		dutchAuctionItem.setHighestBidder(winningBidder);
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.makePayment(wrongBidder);
		});

		assertEquals("You must be the winning bidder to place a payment on this item.", exception.getMessage());
	}

	@Test
	void testMakePaymentWhenAuctionNotSold() {
		User bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		dutchAuctionItem.setHighestBidder(bidder);
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.makePayment(bidder);
		});

		assertEquals("The auction is either over or still ongoing.", exception.getMessage());
	}

	@Test
	void testAuctionStatusTransitions() {

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());

		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		assertEquals(AuctionItem.AuctionStatus.SOLD, dutchAuctionItem.getAuctionStatus());

		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.PAID);
		assertEquals(AuctionItem.AuctionStatus.PAID, dutchAuctionItem.getAuctionStatus());

		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.EXPIRED);
		assertEquals(AuctionItem.AuctionStatus.EXPIRED, dutchAuctionItem.getAuctionStatus());

		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.CANCELLED);
		assertEquals(AuctionItem.AuctionStatus.CANCELLED, dutchAuctionItem.getAuctionStatus());
	}
}