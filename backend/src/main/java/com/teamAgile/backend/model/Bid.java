package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bids")
public class Bid {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bidId", nullable = false)
	private UUID bidID;

	@Column(name = "itemId", nullable = false)
	private UUID itemID;

	@Column(name = "userId", nullable = false)
	private UUID userID;

	@Column(name = "bidAmount", nullable = false)
	private Double bidAmount;
	
	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp = LocalDateTime.now();

	public Bid() {
	}
	
	public Bid(UUID itemID, UUID userID, Double bidAmount) {
		this.itemID = itemID;
		this.userID = userID;
		this.bidAmount = bidAmount;
	}

	public UUID getBidID() {
		return bidID;
	}

	public UUID getItemID() {
		return itemID;
	}

	public UUID getUserID() {
		return userID;
	}

	public Double getBidAmount() {
		return bidAmount;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setItemID(UUID itemID) {
		this.itemID = itemID;
	}

	public void setUserID(UUID userID) {
		this.userID = userID;
	}

	public void setBidAmount(Double bidAmount) {
		this.bidAmount = bidAmount;
	}
	
	public void setTimestamp() {
		this.timestamp = LocalDateTime.now();
	}
}
