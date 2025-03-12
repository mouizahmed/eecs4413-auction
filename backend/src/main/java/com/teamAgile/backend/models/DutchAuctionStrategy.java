package com.teamAgile.backend.models;

public class DutchAuctionStrategy implements AuctionStrategy {
	@Override
	public void placeBid(AuctionItem item, double newBid, String username) {
		if (item.getAuctionStatus() == AuctionItem.AuctionStatus.SOLD
				|| item.getAuctionStatus() == AuctionItem.AuctionStatus.EXPIRED) {
			throw new IllegalStateException("Auction is already closed.");
		}

		item.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		item.setHighestBidder(username);
	}
}
