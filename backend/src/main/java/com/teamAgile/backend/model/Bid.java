package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bidId", nullable = false)
    private UUID bidID;

    @Column(name = "itemId", nullable = false)
    private UUID itemID;

    // Establish many-to-one relationship with User
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "bidAmount", nullable = false)
    private Double bidAmount;

    // Let Hibernate handle timestamp creation
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Bid() {
    }
    
    public Bid(UUID itemID, User user, Double bidAmount) {
        this.itemID = itemID;
        this.user = user;
        this.bidAmount = bidAmount;
    }

    public UUID getBidID() {
        return bidID;
    }

    public UUID getItemID() {
        return itemID;
    }

    public User getUser() {
        return user;
    }

    public Double getBidAmount() {
        return bidAmount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setItemID(UUID itemID) {
        this.itemID = itemID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }
}
