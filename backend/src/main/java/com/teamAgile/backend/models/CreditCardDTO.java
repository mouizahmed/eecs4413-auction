package com.teamAgile.backend.models;

import java.time.YearMonth;
import java.util.UUID;

public class CreditCardDTO {
	private String cardNumber;
	private String cardHolderName;
	private YearMonth expiryDate;
	private String securityCode;

	public CreditCardDTO() {
	}

	public CreditCardDTO(String cardNumber, String cardHolderName, YearMonth expiryDate, String securityCode) {
		this.cardNumber = cardNumber;
		this.cardHolderName = cardHolderName;
		this.expiryDate = expiryDate;
		this.securityCode = securityCode;
	}

	// Getters and setters
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public YearMonth getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(YearMonth expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}
