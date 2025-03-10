package com.a2.bidding;

import java.time.LocalDateTime;

public class Bid {
    private Long id;
    private Long itemId;
    private Long bidderId;
    private double bidAmount;
    private LocalDateTime bidTime;

    // Constructors, Getters, and Setters
    public Bid() {}

    public Bid(Long id, Long itemId, Long bidderId, double bidAmount, LocalDateTime bidTime) {
        this.id = id;
        this.itemId = itemId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }
    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
}
