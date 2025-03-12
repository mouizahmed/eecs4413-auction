package com.teamAgile.backend.models;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class ForwardAuctionStrategy implements AuctionStrategy {
	@Override
	public void placeBid(AuctionItem item, double newBid, String username) {
		if (item.getEndTime() == null || LocalDateTime.now().isAfter(item.getEndTime())) {
			throw new IllegalStateException("Auction has already ended!");
		}
		if (newBid <= item.getCurrentPrice()) {
			throw new IllegalArgumentException("New bid must be higher than the current price!");
		}

		item.setCurrentPrice(newBid);
		item.setHighestBidder(username);
	}
}
