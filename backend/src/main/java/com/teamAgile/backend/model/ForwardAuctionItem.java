package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FORWARD")
public class ForwardAuctionItem extends AuctionItem {
	@Column(name = "endTime")
	private LocalDateTime endTime;

	public ForwardAuctionItem() {

	}

	public ForwardAuctionItem(String itemName, User seller, AuctionStatus auctionStatus, Double currentPrice,
			Integer shippingTime, LocalDateTime endTime) {
		super(itemName, seller, AuctionType.FORWARD, auctionStatus, currentPrice, shippingTime);
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
}
