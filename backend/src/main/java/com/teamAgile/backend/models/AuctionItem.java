package com.teamAgile.backend.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auctionItems")
public class AuctionItem {
	public enum AuctionType {
		FORWARD, DUTCH
	}

	public enum AuctionStatus {
		AVAILABLE, SOLD, EXPIRED, CANCELLED, PENDING_PAYMENT, PAID, SHIPPED, CLOSED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "itemid", nullable = false)
	private UUID itemID;

	@Column(name = "itemname", nullable = false)
	private String itemName;

	@Column(name = "currentprice", nullable = false)
	private double currentPrice;

	@Column(name = "auctiontype", nullable = false)
	private AuctionType auctionType;

	@Column(name = "auctionstatus", nullable = false)
	private AuctionStatus auctionStatus;

	@Column(name = "sellerid", nullable = false)
	private UUID sellerID;

	@Column(name = "highestbidder")
	private String highestBidder;

	@Column(name = "endtime")
	private LocalDateTime endTime;

	@Column(name = "reserveprice")
	private Double reservePrice;

	private transient AuctionStrategy auctionStrategy;

	public AuctionItem() {

	}

	public AuctionItem(AuctionType auctionType) {
		this.auctionType = auctionType;
		this.auctionStatus = AuctionStatus.AVAILABLE;
		this.auctionStrategy = selectStrategy(auctionType);
	}

	public UUID getItemID() {
		return itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public AuctionType getAuctionType() {
		return auctionType;
	}

	public AuctionStatus getAuctionStatus() {
		return auctionStatus;
	}

	public UUID getSellerID() {
		return sellerID;
	}

	public String getHighestBidder() {
		return highestBidder;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public Double getReservePrice() {
		return reservePrice;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public void setAuctionStatus(AuctionStatus auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	public void setHighestBidder(String highestBidder) {
		this.highestBidder = highestBidder;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setReservePrice(Double reservePrice) {
		this.reservePrice = reservePrice;
	}

	public void placeBid(double newBid, String username) {
		auctionStrategy.placeBid(this, newBid, username);
	}

	private AuctionStrategy selectStrategy(AuctionType auctionType) {
		return switch (auctionType) {
		case FORWARD -> new ForwardAuctionStrategy();
		case DUTCH -> new DutchAuctionStrategy();
		};
	}

	public void setAuctionType(AuctionType auctionType) {
		this.auctionType = auctionType;
		
	}
}
