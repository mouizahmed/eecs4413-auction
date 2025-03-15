package com.teamAgile.backend.model;

import java.time.YearMonth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CreditCard {

	@Column(name = "cardNum", nullable = false)
	private String cardNum;

	@Column(name = "cardName", nullable = false)
	private String cardName;

	@Column(name = "expDate", nullable = false)
	private YearMonth expDate;

	@Column(name = "securityCode", nullable = false)
	private String securityCode;

	public CreditCard() {
	}

	public CreditCard(String cardNum, String cardName, YearMonth expDate, String securityCode) {
		this.cardNum = cardNum;
		this.cardName = cardName;
		this.expDate = expDate;
		this.securityCode = securityCode;
	}

	// getters

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

	// setters

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

}
