package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;
import com.teamAgile.backend.model.builder.AuctionItemBuilder;

import jakarta.validation.Valid;

@Service
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final AuctionWebSocketHandler auctionWebSocketHandler;
	private final BidService bidService;

	@Autowired
	public AuctionService(AuctionRepository auctionRepository, AuctionWebSocketHandler auctionWebSocketHandler,
			@Lazy BidService bidService) {
		this.auctionRepository = auctionRepository;
		this.auctionWebSocketHandler = auctionWebSocketHandler;
		this.bidService = bidService;
	}

	public List<AuctionItem> getAllAuctionItems() {
		return auctionRepository.findAll();
	}

	public List<AuctionItem> searchByKeyword(String keyword) {
		List<AuctionItem> items = auctionRepository.findByItemNameContainingIgnoreCase(keyword);
		return items;
	}

	public AuctionItem getAuctionItemByID(UUID itemID) {
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemID);
		if (itemOptional.isEmpty()) {
			return null;
		}

		return itemOptional.get();
	}

	public AuctionItem getAuctionItemWithBidsByID(UUID itemID) {
		AuctionItem item = getAuctionItemByID(itemID);
		if (item == null) {
			return null;
		}

		List<Bid> bids = bidService.getBidsByItemId(itemID);

		item.setBids(bids);

		return item;
	}

	public AuctionItem getAuctionItemByName(String itemName) {
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemName(itemName);

		if (itemOptional.isEmpty()) {
			return null;
		}

		return itemOptional.get();
	}

	public AuctionItem createForwardItem(ForwardItemDTO forwardItemDTO, User user) {
		Optional<?> existingItem = auctionRepository.findByItemName(forwardItemDTO.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");

		if (forwardItemDTO.getAuctionType() == AuctionItem.AuctionType.DUTCH)
			throw new IllegalArgumentException("Cannot create a Forward auction item with DUTCH auction type.");

		if (forwardItemDTO.getEndTime() == null)
			throw new IllegalArgumentException("Forward auction items must have an end time.");

		AuctionItem forwardItem = AuctionItemBuilder
				.forwardAuction(forwardItemDTO.getItemName(), user, forwardItemDTO.getCurrentPrice(),
						forwardItemDTO.getShippingTime())
				.withStatus(forwardItemDTO.getAuctionStatus()).withEndTime(forwardItemDTO.getEndTime()).build();

		AuctionItem savedItem = auctionRepository.save(forwardItem);

		return savedItem;
	}

	public AuctionItem createDutchItem(DutchItemDTO dutchItemDTO, User user) {
		Optional<?> existingItem = auctionRepository.findByItemName(dutchItemDTO.getItemName());
		if (existingItem.isPresent())
			throw new IllegalArgumentException("Auction item already exists.");

		if (dutchItemDTO.getAuctionType() == AuctionItem.AuctionType.FORWARD)
			throw new IllegalArgumentException("Cannot create a Dutch auction item with FORWARD auction type.");

		if (dutchItemDTO.getReservePrice() == null)
			throw new IllegalArgumentException("Dutch auction items must have a reserve price.");

		AuctionItem dutchItem = AuctionItemBuilder
				.dutchAuction(dutchItemDTO.getItemName(), user, dutchItemDTO.getCurrentPrice(),
						dutchItemDTO.getShippingTime())
				.withStatus(dutchItemDTO.getAuctionStatus()).withReservePrice(dutchItemDTO.getReservePrice()).build();

		AuctionItem savedItem = auctionRepository.save(dutchItem);

		return savedItem;
	}

	public AuctionItem decreaseDutchPrice(UUID itemID, UUID userID, Double decreaseBy) {
		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemID);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		if (!item.getSeller().getUserID().equals(userID))
			throw new IllegalArgumentException("You must be the seller to decrease the price.");
		if (item.getAuctionStatus() != AuctionItem.AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is no longer available to make changes.");

		if (!(item instanceof DutchAuctionItem))
			throw new IllegalArgumentException("This operation is only available for Dutch auctions");

		DutchAuctionItem dutchItem = (DutchAuctionItem) item;
		dutchItem.decreasePrice(decreaseBy);

		AuctionItem savedItem = auctionRepository.save(dutchItem);

		auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);

		return savedItem;
	}

	public AuctionItem saveAuctionItem(AuctionItem item) {
		// Just save the item as is - this is an update operation
		return auctionRepository.save(item);
	}

	public AuctionItem getAuctionItemById(UUID itemId) {
		return getAuctionItemByID(itemId);
	}

	public List<AuctionItem> getUnpaidItemsForUser(User user) {
		return auctionRepository.findByHighestBidderAndAuctionStatus(user, AuctionItem.AuctionStatus.SOLD);
	}

	public List<AuctionItem> getAvailableAuctionItems() {
		return auctionRepository.findByAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
	}

	public List<AuctionItem> searchAvailableByKeyword(String keyword) {
		return auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus(keyword,
				AuctionItem.AuctionStatus.AVAILABLE);
	}

	public List<AuctionItem> getWonAuctionsForUser(User user) {
		List<AuctionItem> soldItems = auctionRepository.findByHighestBidderAndAuctionStatus(user,
				AuctionItem.AuctionStatus.SOLD);
		List<AuctionItem> paidItems = auctionRepository.findByHighestBidderAndAuctionStatus(user,
				AuctionItem.AuctionStatus.PAID);
		soldItems.addAll(paidItems);
		return soldItems;
	}

	public List<AuctionItem> getActiveBidsForUser(User user) {
		return auctionRepository.findByUserBidsAndStatus(user, AuctionItem.AuctionStatus.AVAILABLE);
	}

	public List<AuctionItem> getItemsBySeller(User seller) {
		return auctionRepository.findBySeller(seller);
	}
}
