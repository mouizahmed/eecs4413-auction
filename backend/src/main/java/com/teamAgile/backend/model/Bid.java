package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "bids")
public class Bid {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bidId", nullable = false)
	private UUID bidID;

	@Column(name = "itemId", nullable = false)
	private UUID itemID;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonManagedReference(value = "user-bids")
	private User user;

	@Column(name = "bidAmount", nullable = false)
	private Double bidAmount;

	@CreationTimestamp
	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	public Bid() {
	}

	public Bid(UUID itemID, User user, Double bidAmount) {
		if (bidAmount <= 0) {
			throw new IllegalArgumentException("Bid amount must be positive");
		}
		this.itemID = itemID;
		this.user = user;
		this.bidAmount = bidAmount;
		this.timestamp = LocalDateTime.now();
		user.addBid(this);
	}

	public UUID getBidID() {
		return bidID;
	}

	public UUID getItemID() {
		return itemID;
	}

	public User getUser() {
		return user;
	}

	public Double getBidAmount() {
		return bidAmount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setItemID(UUID itemID) {
		if (itemID == null) {
			throw new IllegalArgumentException("Item ID cannot be null");
		}
		this.itemID = itemID;
	}

	public void setUser(User user) {
		if (this.user != null && this.user != user) {
			List<Bid> oldUserBids = this.user.getBids();
			oldUserBids.remove(this);
		}
		this.user = user;
		if (user != null && !user.getBids().contains(this)) {
			user.getBids().add(this);
		}
	}

	public void setBidAmount(Double bidAmount) {
		if (bidAmount == null) {
			throw new IllegalArgumentException("Bid amount cannot be null");
		}
		if (bidAmount <= 0) {
			throw new IllegalArgumentException("Bid amount must be positive");
		}
		this.bidAmount = bidAmount;
	}
}
