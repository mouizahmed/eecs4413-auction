package com.teamAgile.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;

@Service
public class AuctionSchedulerService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    @Autowired
    public AuctionSchedulerService(AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkAndUpdateExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionItem> allItems = auctionRepository.findAll();

        for (AuctionItem item : allItems) {
            if (item instanceof ForwardAuctionItem) {
                ForwardAuctionItem forwardItem = (ForwardAuctionItem) item;

                // Only process items that are still available
                if (forwardItem.getAuctionStatus() == AuctionStatus.AVAILABLE) {
                    // Check if the auction has ended
                    if (forwardItem.getEndTime() != null || now.isAfter(forwardItem.getEndTime())) {
                        // Check if there are any bids
                        boolean hasBids = !bidRepository.findByItemID(forwardItem.getItemID()).isEmpty();

                        // Update status based on whether there are bids
                        if (hasBids) {
                            forwardItem.setAuctionStatus(AuctionStatus.SOLD);
                        } else {
                            forwardItem.setAuctionStatus(AuctionStatus.EXPIRED);
                        }

                        auctionRepository.save(forwardItem);
                    }
                }
            }
        }
    }
}