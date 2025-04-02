package com.teamAgile.backend.model.strategy;

import com.teamAgile.backend.model.*;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;

public class DutchBidStrategy implements BidStrategy {

	@Override
	public void placeBid(AuctionItem auctionItem, Double bidAmount, User user) {
		if (!(auctionItem instanceof DutchAuctionItem)) {
			throw new IllegalArgumentException("This strategy can only be used with Dutch auctions");
		}

		if (auctionItem.getAuctionStatus() != AuctionStatus.AVAILABLE) {
			throw new IllegalArgumentException("Auction Item is currently not available.");
		}

		if (!bidAmount.equals(auctionItem.getCurrentPrice())) {
			throw new IllegalArgumentException("For Dutch auctions, bid amount must equal the current price.");
		}

		Bid bid = new Bid(auctionItem.getItemID(), user, bidAmount);
		auctionItem.getBids().add(bid);
		auctionItem.setHighestBidder(user);
		auctionItem.setAuctionStatus(AuctionStatus.SOLD);
	}
}