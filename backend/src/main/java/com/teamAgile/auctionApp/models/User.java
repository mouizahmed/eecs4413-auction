package com.teamAgile.auctionApp.models;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.teamAgile.auctionApp.utils.BCryptHashing;

public class User {
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private Address address;
	private UUID userID;
	
	public UUID getUserID() {
		return this.userID;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}
	
	public Address getAddress() {
		return this.address;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setUserID(UUID userID) {
		this.userID = userID;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = BCryptHashing.hashPassword(password);
	}

}
