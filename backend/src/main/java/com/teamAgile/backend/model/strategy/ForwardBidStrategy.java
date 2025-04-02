package com.teamAgile.backend.model.strategy;

import java.time.LocalDateTime;

import com.teamAgile.backend.model.*;
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
        } else if (auctionItem.getHighestBidder() != null
                && auctionItem.getHighestBidder().getUserID().equals(user.getUserID())) {
            throw new IllegalArgumentException("You are already the highest bidder on this item.");
        }

        Bid bid = new Bid(auctionItem.getItemID(), user, bidAmount);
        auctionItem.getBids().add(bid);
        auctionItem.setHighestBidder(user);
        auctionItem.setCurrentPrice(bidAmount);
    }
}