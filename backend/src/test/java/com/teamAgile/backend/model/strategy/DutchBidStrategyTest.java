package com.teamAgile.backend.model.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.User;

class DutchBidStrategyTest {

	private DutchBidStrategy strategy;
	private DutchAuctionItem auctionItem;
	private User seller;
	private User bidder;
	private Double currentPrice;
	private Double reservePrice;

	@BeforeEach
	void setUp() throws Exception {
		strategy = new DutchBidStrategy();

		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");

		bidder = new User();
		bidder.setUserID(UUID.randomUUID());
		bidder.setUsername("testBidder");

		currentPrice = 200.0;
		reservePrice = 100.0;

		auctionItem = new DutchAuctionItem();
		auctionItem.setItemName("Test Dutch Item");
		auctionItem.setCurrentPrice(currentPrice);
		auctionItem.setSeller(seller);
		auctionItem.setShippingTime(5);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
		auctionItem.setReservePrice(reservePrice);

		Field field = AuctionItem.class.getDeclaredField("auctionType");
		field.setAccessible(true);
		field.set(auctionItem, AuctionItem.AuctionType.DUTCH);
	}

	@Test
	void testPlaceBidSuccessful() {
		Double bidAmount = currentPrice;
		strategy.placeBid(auctionItem, bidAmount, bidder);

		assertEquals(bidAmount, auctionItem.getCurrentPrice());
		assertEquals(bidder, auctionItem.getHighestBidder());
		assertEquals(AuctionItem.AuctionStatus.SOLD, auctionItem.getAuctionStatus());
	}

	@Test
	void testPlaceBidWithDifferentAmount() {
		Double bidAmount = currentPrice - 10.0;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(auctionItem, bidAmount, bidder);
		});

		assertEquals("For Dutch auctions, bid amount must equal the current price.", exception.getMessage());
		assertEquals(currentPrice, auctionItem.getCurrentPrice());
		assertNull(auctionItem.getHighestBidder());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, auctionItem.getAuctionStatus());

	}

	@Test
	void testPlaceBidWhenAuctionNotAvailable() {
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		Double bidAmount = currentPrice;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(auctionItem, bidAmount, bidder);
		});

		assertEquals("Auction Item is currently not available.", exception.getMessage());
	}

	@Test
	void testPlaceBidWithWrongAuctionType() {

		AuctionItem mockItem = new AuctionItem() {

		};

		Double bidAmount = currentPrice;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			strategy.placeBid(mockItem, bidAmount, bidder);
		});

		assertEquals("This strategy can only be used with Dutch auctions", exception.getMessage());
	}
}