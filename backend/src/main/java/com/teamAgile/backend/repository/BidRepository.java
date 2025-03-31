package com.teamAgile.backend.repository;

import org.springframework.stereotype.Repository;

import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
	List<Bid> findAll();

	List<Bid> findByUser_UserID(UUID userID);

	List<Bid> findByItemIDOrderByBidAmountDesc(UUID itemID);

	Optional<Bid> findByBidID(UUID bidID);
	
	Optional<Bid> findByItemID(UUID itemID);
}
