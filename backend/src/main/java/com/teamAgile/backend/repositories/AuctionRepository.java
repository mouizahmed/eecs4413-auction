package com.teamAgile.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamAgile.backend.models.AuctionItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionItem, UUID> {
    Optional<AuctionItem> findByItemName(String itemName);
    List<AuctionItem> findAll();
}
