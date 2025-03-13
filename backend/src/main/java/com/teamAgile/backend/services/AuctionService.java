package com.teamAgile.backend.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.models.AuctionItem;
import com.teamAgile.backend.models.DutchAuctionItem;
import com.teamAgile.backend.repositories.AuctionRepository;

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

	// public AuctionItem createForwardItem(AuctionItem auctionItem) {
	// if (auctionRepository.findByItemName(auctionItem.getItemName()).isPresent())
	// {
	// return null;
	// }
	// if (auctionItem.getAuctionType() == AuctionItem.AuctionType.DUTCH) throw new
	// IllegalArgumentException("Auction item with the same name already exists.");
	// if (auctionItem.getReservePrice() != null) throw new
	// IllegalArgumentException("Forward auction items cannot have a reserve
	// price.");
	// if (auctionItem.getEndTime() == null) throw new
	// IllegalArgumentException("Forward auction items must have an end time.");
	// auctionItem.setAuctionType(AuctionItem.AuctionType.FORWARD);
	// auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
	//
	// return auctionRepository.save(auctionItem);
	// }

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

}
