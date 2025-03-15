package com.teamAgile.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SignUpDTO {

	@NotBlank(message = "firstName is required.")
	private String firstName;

	@NotBlank(message = "lastName is required.")
	private String lastName;

	@NotBlank(message = "username is required")
	private String username;

	@NotBlank(message = "password is required")
	private String password;

	@NotBlank(message = "streetName is required")
	private String streetName;

	@NotNull(message = "streetNum is required")
	private Integer streetNum;

	@NotBlank(message = "postalCode is required")
	private String postalCode;

	@NotBlank(message = "city is required")
	private String city;

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

	public String getLastName() {
		return lastName;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getStreetName() {
		return streetName;
	}

	public int getStreetNum() {
		return streetNum;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}
	
	public String getSecurityQuestion() {
		return securityQuestion;
	}
	
	public String getSecurityAnswer() {
		return securityAnswer;
	}
}
