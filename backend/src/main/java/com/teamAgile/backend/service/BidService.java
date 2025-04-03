package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;

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

	public Bid createBid(UUID itemId, User user, Double bidAmount) {
		List<AuctionItem> unpaidItems = auctionRepository.findByHighestBidderAndAuctionStatus(user, AuctionStatus.SOLD);
		if (!unpaidItems.isEmpty()) {
			throw new IllegalArgumentException(
					"You have unpaid items. Please pay for your won auctions before placing new bids.");
		}

		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemId);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		Bid bid = new Bid(itemId, user, bidAmount);

		try {
			item.placeBid(bidAmount, user);

			AuctionItem savedItem = auctionRepository.save(item);

			Bid savedBid = bidRepository.save(bid);

			auctionWebSocketHandler.broadcastAuctionUpdate(savedItem);
			auctionWebSocketHandler.broadcastNewBid(savedBid);

			return savedBid;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	public List<Bid> getBidsByItemId(UUID itemId) {
		return bidRepository.findByItemIDOrderByBidAmountDesc(itemId);
	}

	public List<Bid> getBidsByUserId(UUID userId) {
		return bidRepository.findByUser_UserID(userId);
	}

	public Optional<Bid> getBidById(UUID bidId) {
		return bidRepository.findByBidID(bidId);
	}
}
