package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.repository.AuctionRepository;

@Service
public class AuctionService {

	private final AuctionRepository auctionRepository;

	@Autowired
	public AuctionService(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	public List<AuctionItem> getAllAuctionItems() {
		return auctionRepository.findAll();
	}

	public AuctionItem getAuctionItemByName(String name) {
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemName(name);
		if (itemOptional.isEmpty())
			return null;
		return itemOptional.get();
	}
	
	public AuctionItem createForwardItem(ForwardAuctionItem auctionItem, UUID userID) {
		Optional<?> existingItem = auctionRepository.findByItemName(auctionItem.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");
		
		if (auctionItem.getAuctionType() == AuctionItem.AuctionType.DUTCH)
			throw new IllegalArgumentException("Cannot create a Forward auction item with DUTCH auction type.");
		
		if (auctionItem.getEndTime() == null)
			throw new IllegalArgumentException("Forward auction items must have an end time.");
		
		auctionItem.setSellerID(userID);
		
		return auctionRepository.save(auctionItem);
	}

	public AuctionItem createDutchItem(DutchAuctionItem auctionItem, UUID userID) {
		Optional<?> existingItem = auctionRepository.findByItemName(auctionItem.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");

		if (auctionItem.getAuctionType() == AuctionItem.AuctionType.FORWARD)
			throw new IllegalArgumentException("Cannot create a Dutch auction item with FORWARD auction type.");

		if (auctionItem.getReservePrice() == null)
			throw new IllegalArgumentException("Dutch auction items must have a reserve price.");
		
		auctionItem.setSellerID(userID);

		return auctionRepository.save(auctionItem);
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

		return auctionRepository.save(dutchItem);
	}

}
