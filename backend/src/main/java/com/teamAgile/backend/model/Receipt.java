package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.teamAgile.backend.util.CreditCardValidator;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receiptId", nullable = false)
    private UUID receiptID;

    @Column(name = "itemId", nullable = false)
    private UUID itemID;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonManagedReference(value = "user-receipts")
    private User user;

    @Column(name = "totalCost", nullable = false)
    private Double totalCost;

    @Embedded
    private CreditCard creditCard;

    @Embedded
    private Address address;

    @Column(name = "shippingTime", nullable = false)
    private Integer shippingTime;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Receipt() {
    }

    public Receipt(UUID itemID, User user, Double totalCost, CreditCard creditCard, Address address,
            Integer shippingTime) {
        if (itemID == null || user == null || totalCost == null || creditCard == null || address == null
                || shippingTime == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }
        if (totalCost <= 0) {
            throw new IllegalArgumentException("Total cost must be positive");
        }
        if (shippingTime <= 0) {
            throw new IllegalArgumentException("Shipping time must be positive");
        }

        // Validate credit card
        if (!CreditCardValidator.isValidCreditCard(creditCard.getCardNum())) {
            throw new IllegalArgumentException("Invalid credit card number");
        }
        if (creditCard.getExpDate() == null || creditCard.getExpDate().isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Credit card is expired");
        }

        this.itemID = itemID;
        this.user = user;
        this.totalCost = totalCost;
        this.creditCard = creditCard;
        this.address = address;
        this.shippingTime = shippingTime;
        this.timestamp = LocalDateTime.now();
        user.addReceipt(this);
    }

    // Getters
    public UUID getReceiptID() {
        return receiptID;
    }

    public UUID getItemID() {
        return itemID;
    }

    public User getUser() {
        return user;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public Address getAddress() {
        return address;
    }

    public Integer getShippingTime() {
        return shippingTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setReceiptID(UUID receiptID) {
        this.receiptID = receiptID;
    }

    public void setItemID(UUID itemID) {
        if (itemID == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        this.itemID = itemID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTotalCost(Double totalCost) {
        if (totalCost == null) {
            throw new IllegalArgumentException("Total cost cannot be null");
        }
        if (totalCost <= 0) {
            throw new IllegalArgumentException("Total cost must be positive");
        }
        this.totalCost = totalCost;
    }

    public void setCreditCard(CreditCard creditCard) {
        if (creditCard == null) {
            throw new IllegalArgumentException("Credit card cannot be null");
        }
        this.creditCard = creditCard;
    }

    public void setAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        this.address = address;
    }

    public void setShippingTime(Integer shippingTime) {
        if (shippingTime == null) {
            throw new IllegalArgumentException("Shipping time cannot be null");
        }
        if (shippingTime <= 0) {
            throw new IllegalArgumentException("Shipping time must be positive");
        }
        this.shippingTime = shippingTime;
    }
}
