package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "receipts")
public class Receipt {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "receiptId", nullable = false)
	private UUID receiptID;

	@Column(name = "itemId", nullable = false)
	private UUID itemID;

	@Column(name = "userId", nullable = false)
	private UUID userID;

	@Column(name = "totalCost", nullable = false)
	private Double totalCost;

	@Embedded
	private CreditCard creditCard;
	
	@Embedded
	private Address address;

	@Column(name = "shippingTime", nullable = false)
	private Integer shippingTime;

	@CreationTimestamp
	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp = LocalDateTime.now();

	public Receipt() {
	}
	
	public Receipt(UUID itemID, UUID userID, Double totalCost, CreditCard creditCard, Address address, Integer shippingTime) {
		this.itemID = itemID;
		this.userID = itemID;
		this.totalCost = totalCost;
		this.creditCard = creditCard;
		this.address = address;
		this.shippingTime = shippingTime;
	}

	// getters

	public UUID getReceiptID() {
		return receiptID;
	}

	public UUID getItemID() {
		return itemID;
	}

	public UUID getUserID() {
		return userID;
	}

	public Double getTotalCost() {
		return totalCost;
	}
	
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public Address getAddress() {
		return address;
	}

	public Integer getShippingTime() {
		return shippingTime;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	// setters
	public void setReceiptID(UUID receiptID) {
		this.receiptID = receiptID;
	}

	public void setItemID(UUID itemID) {
		this.itemID = itemID;
	}

	public void setUserID(UUID userID) {
		this.userID = userID;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setShippingTime(Integer shippingTime) {
		this.shippingTime = shippingTime;
	}

	public void setTimestamp() {
		this.timestamp = LocalDateTime.now();
	}

}
