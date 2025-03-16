package com.teamAgile.backend.model.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;

class ForwardBidStrategyTest {

	private ForwardBidStrategy strategy;
	private ForwardAuctionItem auctionItem;
	private User seller;
	private User bidder;

	@BeforeEach
	void setUp() throws Exception {
		strategy = new ForwardBidStrategy();

		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");

		bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		auctionItem = new ForwardAuctionItem();
		auctionItem.setItemName("Test Item");
		auctionItem.setCurrentPrice(100.0);
		auctionItem.setSeller(seller);
		auctionItem.setEndTime(LocalDateTime.now().plusDays(7));
		auctionItem.setShippingTime(5);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		Field field = AuctionItem.class.getDeclaredField("auctionType");
		field.setAccessible(true);
		field.set(auctionItem, AuctionItem.AuctionType.FORWARD);
	}

	@Test
	void testPlaceBidSuccessful() {
		Double bidAmount = 150.0;
		strategy.placeBid(auctionItem, bidAmount, bidder);

		assertEquals(bidAmount, auctionItem.getCurrentPrice());
		assertEquals(bidder, auctionItem.getHighestBidder());
	}

	@Test
	void testPlaceBidWithLowerAmount() {
		Double bidAmount = 50.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(auctionItem, bidAmount, bidder);
		});

		assertEquals("Bid price must be greater than the current price.", exception.getMessage());
		assertEquals(100.0, auctionItem.getCurrentPrice());
		assertNull(auctionItem.getHighestBidder());
	}

	@Test
	void testPlaceBidAfterAuctionEnded() throws Exception {

		LocalDateTime pastEndTime = LocalDateTime.now().minusDays(1);

		Field endTimeField = ForwardAuctionItem.class.getDeclaredField("endTime");
		endTimeField.setAccessible(true);
		endTimeField.set(auctionItem, pastEndTime);

		Double bidAmount = 150.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(auctionItem, bidAmount, bidder);
		});

		assertEquals("This forward auction has now closed.", exception.getMessage());
	}

	@Test
	void testPlaceBidWhenAuctionNotAvailable() {
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		Double bidAmount = 150.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(auctionItem, bidAmount, bidder);
		});

		assertEquals("Auction Item is currently not available.", exception.getMessage());
	}

	@Test
	void testPlaceBidWithWrongAuctionType() {

		AuctionItem mockItem = new AuctionItem() {

		};

		Double bidAmount = 150.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(mockItem, bidAmount, bidder);
		});

		assertEquals("This strategy can only be used with forward auctions", exception.getMessage());
	}
}