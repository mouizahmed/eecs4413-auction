package com.teamAgile.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.model.Bid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionItem, UUID> {
    Optional<AuctionItem> findByItemName(String itemName);

    Optional<AuctionItem> findByItemID(UUID itemID);

    List<AuctionItem> findAll();

    List<AuctionItem> findByItemNameContainingIgnoreCase(String keyword);

    List<AuctionItem> findByItemNameContainingIgnoreCaseAndAuctionStatus(String keyword, AuctionStatus auctionStatus);

    List<AuctionItem> findByHighestBidderAndAuctionStatus(User highestBidder, AuctionStatus auctionStatus);

    List<AuctionItem> findByAuctionStatus(AuctionStatus auctionStatus);

    @Query("SELECT DISTINCT a FROM AuctionItem a JOIN Bid b ON a.itemID = b.itemID WHERE b.user = :user AND a.auctionStatus = :status")
    List<AuctionItem> findByUserBidsAndStatus(@Param("user") User user, @Param("status") AuctionStatus status);
}
