package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
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
	@Column(name = "receiptid", nullable = false)
	private UUID receiptID;
	
	@Column(name = "itemid", nullable = false)
	private UUID itemID;
	
	@Column(name = "userid", nullable = false)
	private UUID userID;
	
	@Column(name = "totalCost", nullable = false)
	private Double totalCost;
	
	@Column(name = "cardNum", nullable = false)
	private String cardNum;
	
	@Column(name = "cardname", nullable = false)
	private String cardName;
	
	@Column(name = "expdate", nullable = false)
	private YearMonth expDate;
	
	@Column(name = "securitycode", nullable = false)
	private String securityCode;
	
	@Column(name = "streetname", nullable = false)
	private String streetName;

	@Column(name = "streetnumber", nullable = false)
	private int streetNumber;

	@Column(name = "postalcode", nullable = false)
	private String postalCode;

	@Column(name = "country", nullable = false)
	private String country;
	
	@Column(name = "shippingTime", nullable = false)
	private int shippingTime;
	
	@CreationTimestamp
	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;
	
	public Receipt() {
	}
	
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
	
	public String getCardNum() {
		return cardNum;
	}
	
	public String getCardName() {
		return cardName;
	}
	
	public YearMonth getExpDate() {
		return expDate;
	}
	
	public String getSecurityCode() {
		return securityCode;
	}
	
	public String getStreetName() {
		return streetName;
	}

	public int getStreetNumber() {
		return streetNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountry() {
		return country;
	}
	
	public int getShippingTime() {
		return shippingTime;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
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
	
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	public void setExpDate(YearMonth expDate) {
		this.expDate = expDate;
	}
	
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public void setShippingTime(int shippingTime) {
		this.shippingTime = shippingTime;
	}
	
	public void setTimestamp() {
		this.timestamp = LocalDateTime.now();
	}
	
}
