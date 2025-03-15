package com.teamAgile.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

	@Column(name = "streetName", nullable = false)
	private String streetName;

	@Column(name = "streetNum", nullable = false)
	private Integer streetNum;

	@Column(name = "postalCode", nullable = false)
	private String postalCode;

	@Column(name = "city", nullable = false)
	private String city;

	@Column(name = "country", nullable = false)
	private String country;

	public Address() {
	}

	public Address(String streetName, Integer streetNum, String postalCode, String city, String country) {
		this.streetName = streetName;
		this.streetNum = streetNum;
		this.postalCode = postalCode;
		this.city = city;
		this.country = country;
	}

	// getters

	public String getStreetName() {
		return this.streetName;
	}

	public Integer getStreetNum() {
		return this.streetNum;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public String getCity() {
		return this.city;
	}

	public String getCountry() {
		return this.country;
	}

	// setters
	
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	
	public void setStreetNum(Integer streetNum) {
		this.streetNum = streetNum;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
}
