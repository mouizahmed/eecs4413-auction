package com.teamAgile.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SignUpDTO extends SignInDTO {

	@NotBlank(message = "firstName is required.")
	private String firstName;

	@NotBlank(message = "lastName is required.")
	private String lastName;

	@NotBlank(message = "streetName is required")
	private String streetName;

	@NotNull(message = "streetNum is required")
	private Integer streetNum;

	@NotBlank(message = "postalCode is required")
	private String postalCode;

	@NotBlank(message = "city is required")
	private String city;

	@NotBlank(message = "province is required")
	private String province;

	@NotBlank(message = "country is required")
	private String country;

	@NotBlank(message = "securityQuestion is required")
	private String securityQuestion;

	@NotBlank(message = "securityAnswer is required")
	private String securityAnswer;

	public SignUpDTO() {
	}

	// getters
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public int getStreetNum() {
		return streetNum;
	}

	public void setStreetNum(Integer streetNum) {
		this.streetNum = streetNum;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}
}
