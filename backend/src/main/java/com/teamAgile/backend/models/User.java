package com.teamAgile.backend.models;

import java.util.UUID;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamAgile.backend.utils.BCryptHashing;

@Entity
@Table(name = "users") // Ensure the table name is correct
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userid", nullable = false) // Match DB column
	private UUID userID;

	@Column(name = "firstname", nullable = false) // Ensure correct mapping
	private String firstName;

	@Column(name = "lastname", nullable = false)
	private String lastName;

	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "streetname", nullable = false)
	private String streetName;

	@Column(name = "streetnumber", nullable = false)
	private int streetNumber;

	@Column(name = "postalcode", nullable = false)
	private String postalCode;

	@Column(name = "country", nullable = false)
	private String country;

	// 🔹 Constructors
	public User() {
	}

	// 🔹 Getters and Setters
	public UUID getUserID() {
		return userID;
	}

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

	public int getStreetNumber() {
		return streetNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		if (!password.startsWith("$2a$")) { // Only hash if it's not already hashed
			this.password = BCryptHashing.hashPassword(password);
		} else {
			this.password = password;
		}
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
}
