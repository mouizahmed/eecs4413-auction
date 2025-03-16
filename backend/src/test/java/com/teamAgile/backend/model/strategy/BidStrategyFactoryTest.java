package com.teamAgile.backend.model.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;

class BidStrategyFactoryTest {

	private ForwardAuctionItem forwardAuctionItem;
	private DutchAuctionItem dutchAuctionItem;
	private User seller;

	@BeforeEach
	void setUp() throws Exception {

		seller = new User();
		seller.setUserID(UUID.randomUUID());
		seller.setUsername("testSeller");

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
	void testGetStrategyForForwardAuction() {
		BidStrategy strategy = BidStrategyFactory.getStrategy(forwardAuctionItem);

		assertNotNull(strategy);
		assertTrue(strategy instanceof ForwardBidStrategy);
	}

	@Test
	void testGetStrategyForDutchAuction() {
		BidStrategy strategy = BidStrategyFactory.getStrategy(dutchAuctionItem);

		assertNotNull(strategy);
		assertTrue(strategy instanceof DutchBidStrategy);
	}

	@Test
	void testGetStrategyForUnknownAuctionType() throws Exception {

		AuctionItem mockItem = new AuctionItem() {

			@Override
			public AuctionType getAuctionType() {
				return null;
			}
		};

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BidStrategyFactory.getStrategy(mockItem);
		});

		assertTrue(exception.getMessage().contains("Unknown auction type"));
	}
}