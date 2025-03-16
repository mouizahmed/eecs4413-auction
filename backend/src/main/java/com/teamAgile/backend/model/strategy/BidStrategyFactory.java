package com.teamAgile.backend.model.strategy;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionType;

public class BidStrategyFactory {

	public static BidStrategy getStrategy(AuctionType auctionType) {
		if (auctionType == null) {
			throw new IllegalArgumentException("Unknown auction type: null");
		}

		switch (auctionType) {
			case FORWARD:
				return new ForwardBidStrategy();
			case DUTCH:
				return new DutchBidStrategy();
			default:
				throw new IllegalArgumentException("Unsupported auction type: " + auctionType);
		}
	}

	public static BidStrategy getStrategy(AuctionItem auctionItem) {
		return getStrategy(auctionItem.getAuctionType());
	}
}