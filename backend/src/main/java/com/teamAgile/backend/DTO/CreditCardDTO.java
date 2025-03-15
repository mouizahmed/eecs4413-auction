package com.teamAgile.backend.DTO;

import java.time.YearMonth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreditCardDTO {

	@NotBlank(message = "Card number is mandatory")
	private String cardNum;

	@NotBlank(message = "Card name is mandatory")
	private String cardName;

	@NotNull(message = "Expiration date is mandatory")
	private YearMonth expDate;

	@NotBlank(message = "Security code is mandatory")
	@Pattern(regexp = "\\d{3,4}", message = "Security code must be 3 or 4 digits")
	private String securityCode;

	public CreditCardDTO() {
	}

	public CreditCardDTO(String cardNum, String cardName, YearMonth expDate, String securityCode) {
		this.cardNum = cardNum;
		this.cardName = cardName;
		this.expDate = expDate;
		this.securityCode = securityCode;
	}

	// Getters and setters
	public String getCardNum() {
		return cardNum;
	}

	public void setCardNumber(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public YearMonth getExpDate() {
		return expDate;
	}

	public void setExpDate(YearMonth expDate) {
		this.expDate = expDate;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}
