package com.teamAgile.backend.models;

import java.time.LocalDateTime;

import com.teamAgile.backend.models.AuctionItem.AuctionType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FORWARD")
public class ForwardAuctionItem extends AuctionItem {
	@Column(name = "endtime")
	private LocalDateTime endTime;

	public ForwardAuctionItem() {
		 super(AuctionType.FORWARD);
	}
	
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public void placeBid(double newBid, String username) {
		if (this.getAuctionStatus() != AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is currently not available.");
		if (this.getCurrentPrice() > newBid)
			throw new IllegalArgumentException("Bid price must be greater than the current price.");
		this.setHighestBidder(username);
		this.setCurrentPrice(newBid);
	}
}
