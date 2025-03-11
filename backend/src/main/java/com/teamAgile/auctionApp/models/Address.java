package com.teamAgile.auctionApp.models;

public class Address {
	private String streetName;
	private int streetNumber;
	private String postalCode;
	private String country;

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

	public String getStreetName() {
		return this.streetName;
	}

	public int getStreetNumber() {
		return this.streetNumber;
	}

	public String getPostalCode() {
		return this.postalCode;
	}
	
	public String getCountry() {
		return this.country;
	}
}
