package com.teamAgile.backend.model;

import java.util.UUID;

import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.util.BCryptHashing;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userId", nullable = false)
	private UUID userID;

	@Column(name = "firstName", nullable = false)
	private String firstName;

	@Column(name = "lastName", nullable = false)
	private String lastName;

	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Embedded
	private Address address;
	
	@Column(name = "securityQuestion", nullable = false)
	private String securityQuestion;
	
	@Column(name = "securityAnswer", nullable = false)
	private String securityAnswer;

	public User() {
	}
	
	public User(UUID userID, String firstName, String lastName, String username, String streetName, Integer streetNum, String postalCode, String city, String country) {
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = null;
		this.securityQuestion = null;
		this.securityAnswer = null;
		Address address = new Address(streetName, streetNum, postalCode, city, country);
		this.address = address;
	}
	
	public User(SignUpDTO signUpDTO) {
		this.firstName = signUpDTO.getFirstName();
		this.lastName = signUpDTO.getLastName();
		this.username = signUpDTO.getUsername();
		this.password = BCryptHashing.hashPassword(signUpDTO.getPassword());
		this.securityQuestion = signUpDTO.getSecurityQuestion();
		this.securityAnswer = signUpDTO.getSecurityAnswer();
		Address address = new Address(signUpDTO.getStreetName(), signUpDTO.getStreetNum(), signUpDTO.getPostalCode(), signUpDTO.getCity(), signUpDTO.getCountry());
		this.address = address;
	}

	
	// getters
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

	public Address getAddress() {
		return address;
	}
	
	public String getSecurityQuestion() {
		return securityQuestion;
	}
	
	public String getSecurityAnswer() {
		return securityAnswer;
	}
	
	
	// setters
	public void setUserID(UUID userID) {
		this.userID = userID;
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
		this.password = BCryptHashing.hashPassword(password);
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}
	
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}
}
