package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teamAgile.backend.model.AuctionItem.AuctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FORWARD")
public class ForwardAuctionItem extends AuctionItem {
	@Column(name = "endtime")
	private LocalDateTime endTime;

	public ForwardAuctionItem() {
		
	}
	
	public ForwardAuctionItem(String itemName, UUID sellerID, AuctionStatus auctionStatus, Double currentPrice, Integer shippingTime, LocalDateTime endTime) {
        super(itemName, sellerID, auctionStatus, currentPrice, shippingTime);
        this.endTime = endTime;
    }

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public void placeBid(double newBid, UUID userID) {
		if (this.getAuctionStatus() != AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is currently not available.");
		if (this.getCurrentPrice() > newBid)
			throw new IllegalArgumentException("Bid price must be greater than the current price.");
		this.setHighestBidder(userID);
		this.setCurrentPrice(newBid);
	}
}
