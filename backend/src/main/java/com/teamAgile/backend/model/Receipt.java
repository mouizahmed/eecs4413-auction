package com.teamAgile.backend.model;

import java.time.LocalDateTime;
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
    
    public Receipt(UUID itemID, User user, Double totalCost, CreditCard creditCard, Address address, Integer shippingTime) {
        this.itemID = itemID;
        this.user = user;
        this.totalCost = totalCost;
        this.creditCard = creditCard;
        this.address = address;
        this.shippingTime = shippingTime;
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
        this.itemID = itemID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setShippingTime(Integer shippingTime) {
        this.shippingTime = shippingTime;
    }
}
