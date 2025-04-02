package com.teamAgile.backend.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.util.BCryptHashing;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails, java.io.Serializable {
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
	@JsonIgnore
	private String password;

	@Embedded
	private Address address;

	@Column(name = "securityQuestion", nullable = false)
	private String securityQuestion;

	@Column(name = "securityAnswer", nullable = false)
	@JsonIgnore
	private String securityAnswer;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference(value = "user-receipts")
	private List<Receipt> receipts = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference(value = "user-bids")
	private List<Bid> bids = new ArrayList<>();

	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference(value = "user-auctionItems")
	private List<AuctionItem> auctionItems = new ArrayList<>();

	public User() {
	}

	public User(UUID userID, String firstName, String lastName, String username, String streetName, Integer streetNum,
			String postalCode, String city, String province, String country) {
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = null;
		this.securityQuestion = null;
		this.securityAnswer = null;
		Address address = new Address(streetName, streetNum, postalCode, city, province, country);
		this.address = address;
	}

	public User(SignUpDTO signUpDTO) {
		this.firstName = signUpDTO.getFirstName();
		this.lastName = signUpDTO.getLastName();
		this.username = signUpDTO.getUsername();
		this.password = BCryptHashing.hashPassword(signUpDTO.getPassword());
		this.securityQuestion = signUpDTO.getSecurityQuestion();
		this.setSecurityAnswer(signUpDTO.getSecurityAnswer());
		Address address = new Address(signUpDTO.getStreetName(), signUpDTO.getStreetNum(), signUpDTO.getPostalCode(),
				signUpDTO.getCity(), signUpDTO.getProvince(), signUpDTO.getCountry());
		this.address = address;
	}

	// Getters and setters

	public UUID getUserID() {
		return userID;
	}

	public void setUserID(UUID userID) {
		this.userID = userID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if (firstName == null || firstName.trim().isEmpty()) {
			throw new IllegalArgumentException("First name cannot be null or empty");
		}
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if (lastName == null || lastName.trim().isEmpty()) {
			throw new IllegalArgumentException("Last name cannot be null or empty");
		}
		this.lastName = lastName;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}
		this.password = BCryptHashing.hashPassword(password);
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		if (address == null) {
			throw new IllegalArgumentException("Address cannot be null");
		}
		if (address.getStreetName() == null || address.getStreetName().trim().isEmpty() ||
				address.getStreetNum() == null || address.getStreetNum() <= 0 ||
				address.getPostalCode() == null || !address.getPostalCode().matches("[A-Z][0-9][A-Z][0-9][A-Z][0-9]") ||
				address.getCity() == null || address.getCity().trim().isEmpty() ||
				address.getProvince() == null || address.getProvince().trim().isEmpty() ||
				address.getCountry() == null || address.getCountry().trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid address");
		}
		this.address = address;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		if (securityQuestion == null || securityQuestion.trim().isEmpty()) {
			throw new IllegalArgumentException("Security question cannot be null or empty");
		}
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
			throw new IllegalArgumentException("Security answer cannot be null or empty");
		}
		this.securityAnswer = BCryptHashing.hashPassword(securityAnswer);
	}

	public boolean checkSecurityAnswer(String answer) {
		return BCryptHashing.checkPassword(answer, this.securityAnswer);
	}

	// UserDetails implementation
	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}

	// Getters and setters for receipts
	public List<Receipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<Receipt> receipts) {
		this.receipts = receipts;
	}

	public void addReceipt(Receipt receipt) {
		if (receipt == null) {
			throw new IllegalArgumentException("Receipt cannot be null");
		}
		receipts.add(receipt);
		if (receipt.getUser() != this) {
			receipt.setUser(this);
		}
	}

	public void removeReceipt(Receipt receipt) {
		if (receipt == null) {
			throw new IllegalArgumentException("Receipt cannot be null");
		}
		receipts.remove(receipt);
		if (receipt.getUser() == this) {
			receipt.setUser(null);
		}
	}

	// Getters and setters for bids
	public List<Bid> getBids() {
		return bids;
	}

	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}

	public void addBid(Bid bid) {
		if (bid == null) {
			throw new IllegalArgumentException("Bid cannot be null");
		}
		if (!bids.contains(bid)) {
			bids.add(bid);
			if (bid.getUser() != this) {
				bid.setUser(this);
			}
		}
	}

	public void removeBid(Bid bid) {
		if (bid == null) {
			throw new IllegalArgumentException("Bid cannot be null");
		}
		if (bids.remove(bid)) {
			if (bid.getUser() == this) {
				bid.setUser(null);
			}
		}
	}

	public List<AuctionItem> getAuctionItems() {
		return auctionItems;
	}

	public void setAuctionItems(List<AuctionItem> auctionItems) {
		this.auctionItems = auctionItems;
	}

	public void addAuctionItem(AuctionItem auctionItem) {
		if (auctionItem == null) {
			throw new IllegalArgumentException("Auction item cannot be null");
		}
		auctionItems.add(auctionItem);
		if (auctionItem.getSeller() != this) {
			auctionItem.setSeller(this);
		}
	}

	public void removeAuctionItem(AuctionItem auctionItem) {
		if (auctionItem == null) {
			throw new IllegalArgumentException("Auction item cannot be null");
		}
		auctionItems.remove(auctionItem);
		if (auctionItem.getSeller() == this) {
			auctionItem.setSeller(null);
		}
	}
}
