package com.teamAgile.backend.models;

public interface AuctionStrategy {
	void placeBid(AuctionItem item, double newBid, String username);
}
