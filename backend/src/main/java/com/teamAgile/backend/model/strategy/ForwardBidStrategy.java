package com.teamAgile.backend.model.strategy;

import java.time.LocalDateTime;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;

public class ForwardBidStrategy implements BidStrategy {

    @Override
    public void placeBid(AuctionItem auctionItem, Double bidAmount, User user) {
        if (!(auctionItem instanceof ForwardAuctionItem)) {
            throw new IllegalArgumentException("This strategy can only be used with forward auctions");
        }

        ForwardAuctionItem forwardItem = (ForwardAuctionItem) auctionItem;

        if (auctionItem.getAuctionStatus() != AuctionStatus.AVAILABLE) {
            throw new IllegalArgumentException("Auction Item is currently not available.");
        } else if (auctionItem.getCurrentPrice() >= bidAmount) {
            throw new IllegalArgumentException("Bid price must be greater than the current price.");
        } else if (LocalDateTime.now().isAfter(forwardItem.getEndTime())) {
            throw new IllegalArgumentException("This forward auction has now closed.");
        }

        auctionItem.setHighestBidder(user);
        auctionItem.setCurrentPrice(bidAmount);
    }
}