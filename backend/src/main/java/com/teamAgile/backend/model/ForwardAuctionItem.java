package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
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
        super(itemName, sellerID, AuctionType.FORWARD, auctionStatus, currentPrice, shippingTime);
        if (endTime.isBefore(LocalDateTime.now())) {
        	throw new IllegalArgumentException("End time must be in the future.");
        }
        this.endTime = endTime;
    }

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		if (endTime.isBefore(LocalDateTime.now())) {
        	throw new IllegalArgumentException("End time must be in the future.");
        }
		this.endTime = endTime;
	}

	@Override
	public void placeBid(Double bidAmount, UUID userID) {
		if (this.getAuctionStatus() != AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is currently not available.");
		else if (this.getCurrentPrice() > bidAmount)
			throw new IllegalArgumentException("Bid price must be greater than the current price.");
		else if (LocalDateTime.now().isAfter(this.getEndTime()))
			throw new IllegalArgumentException("This forward auction has now closed.");
		
		this.setHighestBidder(userID);
		this.setCurrentPrice(bidAmount);
	}
}
