package com.teamAgile.backend.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.models.Bid;
import com.teamAgile.backend.models.AuctionItem;
import com.teamAgile.backend.models.DutchAuctionItem;
import com.teamAgile.backend.repositories.BidRepository;
import com.teamAgile.backend.repositories.AuctionRepository;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    public Bid createBid(UUID itemId, UUID userId, double bidPrice) {
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
