package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

import jakarta.validation.Valid;

@Service
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final AuctionWebSocketHandler auctionWebSocketHandler;

	@Autowired
	public AuctionService(AuctionRepository auctionRepository, AuctionWebSocketHandler auctionWebSocketHandler) {
		this.auctionRepository = auctionRepository;
		this.auctionWebSocketHandler = auctionWebSocketHandler;
	}

	public List<AuctionItem> getAllAuctionItems() {
		return auctionRepository.findAll();
	}

	public List<AuctionItem> searchByKeyword(String keyword) {
		List<AuctionItem> items = auctionRepository.findByItemNameContainingIgnoreCase(keyword);
		return items;
	}

	public AuctionItem createForwardItem(ForwardItemDTO forwardItemDTO, UUID userID) {
		Optional<?> existingItem = auctionRepository.findByItemName(forwardItemDTO.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");

		if (forwardItemDTO.getAuctionType() == AuctionItem.AuctionType.DUTCH)
			throw new IllegalArgumentException("Cannot create a Forward auction item with DUTCH auction type.");

		if (forwardItemDTO.getEndTime() == null)
			throw new IllegalArgumentException("Forward auction items must have an end time.");

		
		
		ForwardAuctionItem forwardItem = new ForwardAuctionItem(forwardItemDTO.getItemName(), userID, forwardItemDTO.getAuctionStatus(), forwardItemDTO.getCurrentPrice(), forwardItemDTO.getShippingTime(), forwardItemDTO.getEndTime());

		AuctionItem savedItem = auctionRepository.save(forwardItem);

		return savedItem;
	}

	public AuctionItem createDutchItem(DutchItemDTO dutchItemDTO, UUID userID) {
		Optional<?> existingItem = auctionRepository.findByItemName(dutchItemDTO.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");

		if (dutchItemDTO.getAuctionType() == AuctionItem.AuctionType.FORWARD)
			throw new IllegalArgumentException("Cannot create a Dutch auction item with FORWARD auction type.");

		if (dutchItemDTO.getReservePrice() == null)
			throw new IllegalArgumentException("Dutch auction items must have a reserve price.");

		DutchAuctionItem dutchItem = new DutchAuctionItem(dutchItemDTO.getItemName(), userID, dutchItemDTO.getAuctionStatus(), dutchItemDTO.getCurrentPrice(), dutchItemDTO.getShippingTime(), dutchItemDTO.getReservePrice());

		AuctionItem savedItem = auctionRepository.save(dutchItem);

		// Broadcast the new item creation
		auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);

		return savedItem;
	}

	public AuctionItem decreaseDutchPrice(UUID itemID, UUID userID, Double decreaseBy) {
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemID);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		if (!item.getSellerID().equals(userID))
			throw new IllegalArgumentException("You must be the seller to decrease the price.");
		if (item.getAuctionStatus() != AuctionItem.AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is no longer available to make changes.");

		if (!(item instanceof DutchAuctionItem))
			throw new IllegalArgumentException("This operation is only available for Dutch auctions");

		DutchAuctionItem dutchItem = (DutchAuctionItem) item;
		dutchItem.decreasePrice(decreaseBy);

		AuctionItem savedItem = auctionRepository.save(dutchItem);

		// Broadcast the price update
		auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);

		return savedItem;
	}
}
