package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuctionIntegrationTest {

	private ForwardAuctionItem forwardAuctionItem;
	private DutchAuctionItem dutchAuctionItem;
	private User seller;
	private User bidder1;
	private User bidder2;

	@BeforeEach
	void setUp() throws Exception {

		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");

		bidder1 = new User();
		bidder1.setUserID(UUID.randomUUID());
		bidder1.setUsername("bidder1");

		bidder2 = new User();
		bidder2.setUserID(UUID.randomUUID());
		bidder2.setUsername("bidder2");

		forwardAuctionItem = new ForwardAuctionItem();
		forwardAuctionItem.setItemName("Test Forward Item");
		forwardAuctionItem.setCurrentPrice(100.0);
		forwardAuctionItem.setSeller(seller);
		forwardAuctionItem.setEndTime(LocalDateTime.now().plusDays(7));
		forwardAuctionItem.setShippingTime(5);
		forwardAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		Field forwardField = AuctionItem.class.getDeclaredField("auctionType");
		forwardField.setAccessible(true);
		forwardField.set(forwardAuctionItem, AuctionItem.AuctionType.FORWARD);

		dutchAuctionItem = new DutchAuctionItem();
		dutchAuctionItem.setItemName("Test Dutch Item");
		dutchAuctionItem.setCurrentPrice(200.0);
		dutchAuctionItem.setSeller(seller);
		dutchAuctionItem.setShippingTime(5);
		dutchAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
		dutchAuctionItem.setReservePrice(100.0);

		Field dutchField = AuctionItem.class.getDeclaredField("auctionType");
		dutchField.setAccessible(true);
		dutchField.set(dutchAuctionItem, AuctionItem.AuctionType.DUTCH);
	}

	@Test
	void testForwardAuctionLifecycle() {

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, forwardAuctionItem.getAuctionStatus());
		assertEquals(100.0, forwardAuctionItem.getCurrentPrice());
		assertNull(forwardAuctionItem.getHighestBidder());

		forwardAuctionItem.placeBid(120.0, bidder1);
		assertEquals(120.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder1, forwardAuctionItem.getHighestBidder());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, forwardAuctionItem.getAuctionStatus());

		forwardAuctionItem.placeBid(150.0, bidder2);
		assertEquals(150.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder2, forwardAuctionItem.getHighestBidder());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, forwardAuctionItem.getAuctionStatus());

		forwardAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		assertEquals(AuctionItem.AuctionStatus.SOLD, forwardAuctionItem.getAuctionStatus());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			forwardAuctionItem.makePayment(bidder1);
		});
		assertEquals("You must be the winning bidder to place a payment on this item.", exception.getMessage());

		forwardAuctionItem.makePayment(bidder2);
		assertEquals(AuctionItem.AuctionStatus.PAID, forwardAuctionItem.getAuctionStatus());
	}

	@Test
	void testDutchAuctionLifecycle() {

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());
		assertEquals(200.0, dutchAuctionItem.getCurrentPrice());
		assertNull(dutchAuctionItem.getHighestBidder());

		dutchAuctionItem.decreasePrice(50.0);
		assertEquals(150.0, dutchAuctionItem.getCurrentPrice());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());

		dutchAuctionItem.placeBid(150.0, bidder1);
		assertEquals(150.0, dutchAuctionItem.getCurrentPrice());
		assertEquals(bidder1, dutchAuctionItem.getHighestBidder());
		assertEquals(AuctionItem.AuctionStatus.SOLD, dutchAuctionItem.getAuctionStatus());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.makePayment(bidder2);
		});
		assertEquals("You must be the winning bidder to place a payment on this item.", exception.getMessage());

		dutchAuctionItem.makePayment(bidder1);
		assertEquals(AuctionItem.AuctionStatus.PAID, dutchAuctionItem.getAuctionStatus());
	}

	@Test
	void testDutchAuctionExpiration() {

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, dutchAuctionItem.getAuctionStatus());
		assertEquals(200.0, dutchAuctionItem.getCurrentPrice());

		dutchAuctionItem.decreasePrice(150.0);
		assertEquals(100.0, dutchAuctionItem.getCurrentPrice()); // Should be at reserve price
		assertEquals(AuctionItem.AuctionStatus.EXPIRED, dutchAuctionItem.getAuctionStatus());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			dutchAuctionItem.placeBid(100.0, bidder1);
		});
		assertEquals("Auction Item is currently not available.", exception.getMessage());
	}

	@Test
	void testMultipleBiddersForwardAuction() {

		User bidder3 = new User();
		bidder3.setUserID(UUID.randomUUID());
		bidder3.setUsername("bidder3");

		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, forwardAuctionItem.getAuctionStatus());
		assertEquals(100.0, forwardAuctionItem.getCurrentPrice());

		forwardAuctionItem.placeBid(110.0, bidder1);
		assertEquals(110.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder1, forwardAuctionItem.getHighestBidder());

		forwardAuctionItem.placeBid(120.0, bidder2);
		assertEquals(120.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder2, forwardAuctionItem.getHighestBidder());

		forwardAuctionItem.placeBid(130.0, bidder3);
		assertEquals(130.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder3, forwardAuctionItem.getHighestBidder());

		forwardAuctionItem.placeBid(140.0, bidder1);
		assertEquals(140.0, forwardAuctionItem.getCurrentPrice());
		assertEquals(bidder1, forwardAuctionItem.getHighestBidder());

		forwardAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		forwardAuctionItem.makePayment(bidder1);
		assertEquals(AuctionItem.AuctionStatus.PAID, forwardAuctionItem.getAuctionStatus());
	}
}