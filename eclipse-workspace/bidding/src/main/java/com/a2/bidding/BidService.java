package com.a2.bidding;


import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class BidService {
    private final List<Bid> bids = new ArrayList<>();
    private final AtomicLong bidIdCounter = new AtomicLong(1);

    public Bid placeForwardBid(Bid bid) {
        bid.setId(bidIdCounter.getAndIncrement());
        bids.add(bid);
        return bid;
    }

    public Bid buyNowDutchAuction(Long itemId, Long bidderId) {
        // Logic to handle "Buy Now" for Dutch auctions
        Bid winningBid = new Bid();
        winningBid.setId(bidIdCounter.getAndIncrement());
        winningBid.setItemId(itemId);
        winningBid.setBidderId(bidderId);
        bids.add(winningBid);
        return winningBid;
    }
}