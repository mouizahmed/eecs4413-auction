package com.a2.bidding;

public class Item {
    private Long id;
    private String name;
    private String description;
    private String auctionType; // e.g., "FORWARD", "DUTCH"
    private double initialPrice;
    private double currentPrice;
    private String endTime; // You may want to use LocalDateTime instead
    private Long sellerId;

    // Constructors, Getters, and Setters
    public Item() {}

    public Item(Long id, String name, String description, String auctionType, double initialPrice, double currentPrice, String endTime, Long sellerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.auctionType = auctionType;
        this.initialPrice = initialPrice;
        this.currentPrice = currentPrice;
        this.endTime = endTime;
        this.sellerId = sellerId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuctionType() { return auctionType; }
    public void setAuctionType(String auctionType) { this.auctionType = auctionType; }
    public double getInitialPrice() { return initialPrice; }
    public void setInitialPrice(double initialPrice) { this.initialPrice = initialPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}