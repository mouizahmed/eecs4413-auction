package com.teamAgile.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    public Bid createForwardBid(UUID itemId, UUID userId, double bidPrice) {
        // Get the auction item
        Optional<AuctionItem> itemOptional = auctionRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("Auction item not found");
        }

        AuctionItem item = itemOptional.get();
        LocalDateTime now = LocalDateTime.now();

        // Check if item is a Auction auction
        if (!(item instanceof ForwardAuctionItem)) {
            throw new IllegalArgumentException("This endpoint is only for Forward auctions");
        }

        ForwardAuctionItem forwardItem = (ForwardAuctionItem) item;

        // Validate bid price matches current price for Dutch auction
        if (bidPrice <= forwardItem.getCurrentPrice()) {
            throw new IllegalArgumentException("Bid price must be greater than the current price for Forward auctions");
        }

        // Check if auction is still available
        if (forwardItem.getAuctionStatus() != AuctionItem.AuctionStatus.AVAILABLE) {
            throw new IllegalArgumentException("Auction is not available for bidding");
        }

        if (now.isAfter(forwardItem.getEndTime())) {
            throw new IllegalArgumentException("This forward auction has now closed.");
        }

        // Create new bid
        Bid bid = new Bid();
        bid.setItemID(itemId);
        bid.setUserID(userId);
        bid.setBidPrice(bidPrice);

        // Place the bid on the auction item
        forwardItem.placeBid(bidPrice, userId.toString());

        // Save both the bid and the updated auction item
        auctionRepository.save(forwardItem);
        return bidRepository.save(bid);
    }

    public Bid createDutchBid(UUID itemId, UUID userId, double bidPrice) {
        // Get the auction item
        Optional<AuctionItem> itemOptional = auctionRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("Auction item not found");
        }

        AuctionItem item = itemOptional.get();

        // Check if item is a Dutch auction
        if (!(item instanceof DutchAuctionItem)) {
            throw new IllegalArgumentException("This endpoint is only for Dutch auctions");
        }

        DutchAuctionItem dutchItem = (DutchAuctionItem) item;

        // Validate bid price matches current price for Dutch auction
        if (bidPrice != dutchItem.getCurrentPrice()) {
            throw new IllegalArgumentException("Bid price must match the current price for Dutch auctions");
        }

        // Check if auction is still available
        if (dutchItem.getAuctionStatus() != AuctionItem.AuctionStatus.AVAILABLE) {
            throw new IllegalArgumentException("Auction is not available for bidding");
        }

        // Create new bid
        Bid bid = new Bid();
        bid.setItemID(itemId);
        bid.setUserID(userId);
        bid.setBidPrice(bidPrice);

        // Place the bid on the auction item
        dutchItem.placeBid(bidPrice, userId.toString());

        // Save both the bid and the updated auction item
        auctionRepository.save(dutchItem);
        return bidRepository.save(bid);
    }

    public List<Bid> getBidsByItemId(UUID itemId) {
        return bidRepository.findByItemID(itemId);
    }

    public List<Bid> getBidsByUserId(UUID userId) {
        return bidRepository.findByUserID(userId);
    }

    public Optional<Bid> getBidById(UUID bidId) {
        return bidRepository.findByBidID(bidId);
    }
}
