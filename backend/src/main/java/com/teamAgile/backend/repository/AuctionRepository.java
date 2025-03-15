package com.teamAgile.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionItem, UUID> {
    Optional<AuctionItem> findByItemName(String itemName);

    Optional<AuctionItem> findByItemID(UUID itemID);

    List<AuctionItem> findAll();

    List<AuctionItem> findByItemNameContainingIgnoreCase(String keyword);
}
