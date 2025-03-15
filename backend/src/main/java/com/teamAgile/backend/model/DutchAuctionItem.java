package com.teamAgile.backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DUTCH")
public class DutchAuctionItem extends AuctionItem {
	@Column(name = "reservePrice")
	private Double reservePrice;

	public DutchAuctionItem() {

	}

	public DutchAuctionItem(String itemName, User seller, AuctionStatus auctionStatus, Double currentPrice,
			Integer shippingTime, Double reservePrice) {
		super(itemName, seller, AuctionType.DUTCH, auctionStatus, currentPrice, shippingTime);
		if (reservePrice >= currentPrice) {
	        throw new IllegalArgumentException("Reserve price must be below the current price.");
	    }
		this.reservePrice = reservePrice;
	}

	public Double getReservePrice() {
		return reservePrice;
	}

	public void setReservePrice(Double reservePrice) {
		if (reservePrice >= this.getCurrentPrice()) {
	        throw new IllegalArgumentException("Reserve price must be below the current price.");
	    }
		this.reservePrice = reservePrice;
	}

	@Override
	public void placeBid(Double bidAmount, User user) {
		if (this.getAuctionStatus() != AuctionStatus.AVAILABLE)
			throw new IllegalArgumentException("Auction Item is currently not available.");
		if (!this.getCurrentPrice().equals(bidAmount))
			throw new IllegalArgumentException("Bid price does not equal to current price.");
		this.setHighestBidder(user);
		this.setAuctionStatus(AuctionStatus.SOLD);
	}

	public void decreasePrice(double lowerBy) {
		double newPrice = this.getCurrentPrice() - lowerBy;
		if (newPrice > this.getReservePrice()) {
			this.setCurrentPrice(newPrice);
		} else {
			this.setCurrentPrice(this.getReservePrice());
			this.setAuctionStatus(AuctionStatus.EXPIRED);
		}
	}
}
