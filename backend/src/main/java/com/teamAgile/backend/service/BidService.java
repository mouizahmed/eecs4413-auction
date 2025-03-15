package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@Service
public class BidService {

	private final BidRepository bidRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionWebSocketHandler auctionWebSocketHandler;

	@Autowired
	public BidService(BidRepository bidRepository, AuctionRepository auctionRepository,
			AuctionWebSocketHandler auctionWebSocketHandler) {
		this.bidRepository = bidRepository;
		this.auctionRepository = auctionRepository;
		this.auctionWebSocketHandler = auctionWebSocketHandler;
	}

	public Bid createForwardBid(UUID itemId, User user, Double bidAmount) {
		// Get the auction item
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemId);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		// Check if item is a Auction auction
		if (!(item instanceof ForwardAuctionItem)) {
			throw new IllegalArgumentException("This endpoint is only for Forward auctions");
		}

		ForwardAuctionItem forwardItem = (ForwardAuctionItem) item;

		// Create new bid
		Bid bid = new Bid(itemId, user, bidAmount);

		// Place the bid on the auction item
		forwardItem.placeBid(bidAmount, user);

		// Save both the bid and the updated auction item
		AuctionItem savedItem = auctionRepository.save(forwardItem);
		Bid savedBid = bidRepository.save(bid);

		// Broadcast the update via WebSocket
		auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);
		auctionWebSocketHandler.broadcastNewBid(savedBid);

		return savedBid;
	}

	public Bid createDutchBid(UUID itemId, User user, Double bidAmount) {
		// Get the auction item
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemId);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		// Check if item is a Dutch auction
		if (!(item instanceof DutchAuctionItem)) {
			throw new IllegalArgumentException("This endpoint is only for Dutch auctions");
		}

		DutchAuctionItem dutchItem = (DutchAuctionItem) item;

		// Create new bid
		Bid bid = new Bid(itemId, user, bidAmount);

		// Place bid
		dutchItem.placeBid(bidAmount, user);

		// Save both the bid and the updated auction item
		AuctionItem savedItem = auctionRepository.save(dutchItem);
		Bid savedBid = bidRepository.save(bid);

		// Broadcast the update via WebSocket
		auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);
		auctionWebSocketHandler.broadcastNewBid(savedBid);

		return savedBid;
	}

	public List<Bid> getBidsByItemId(UUID itemId) {
		return bidRepository.findByItemID(itemId);
	}

	public List<Bid> getBidsByUserId(UUID userId) {
		return bidRepository.findByUser_UserID(userId);
	}

	public Optional<Bid> getBidById(UUID bidId) {
		return bidRepository.findByBidID(bidId);
	}
}
