package com.teamAgile.backend.model;

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
	@Column(name = "bidid", nullable = false)
	private UUID bidID;

	@Column(name = "itemid", nullable = false)
	private UUID itemID;

	@Column(name = "userid", nullable = false)
	private UUID userID;

	@Column(name = "bidprice", nullable = false)
	private double bidPrice;

	public Bid() {
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

	public double getBidPrice() {
		return bidPrice;
	}

	public void setItemID(UUID itemID) {
		this.itemID = itemID;
	}

	public void setUserID(UUID userID) {
		this.userID = userID;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}
}
