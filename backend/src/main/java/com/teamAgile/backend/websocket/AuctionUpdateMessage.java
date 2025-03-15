package com.teamAgile.backend.websocket;

import java.util.UUID;

/**
 * Message class for auction updates sent through WebSocket
 */
public class AuctionUpdateMessage {
    private String type;
    private UUID itemId;
    private String itemName;
    private Double currentPrice;
    private String highestBidder;
    private String auctionStatus;

    // Default constructor for Jackson
    public AuctionUpdateMessage() {
    }

    public AuctionUpdateMessage(String type, UUID itemId, String itemName, Double currentPrice, String highestBidder,
            String auctionStatus) {
        this.type = type;
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.auctionStatus = auctionStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }
}