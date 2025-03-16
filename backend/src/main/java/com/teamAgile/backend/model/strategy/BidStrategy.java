package com.teamAgile.backend.model.strategy;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.User;

public interface BidStrategy {
    void placeBid(AuctionItem auctionItem, Double bidAmount, User user);
}