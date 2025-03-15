package com.teamAgile.backend.websocket;

import java.util.UUID;

public class AuctionUpdateMessage {
	
	public enum AuctionUpdateType {
		AUCTION_UPDATE
	}
	
    private AuctionUpdateType type;
    private UUID itemId;
    private String itemName;
    private Double currentPrice;
    private UUID highestBidder;
    private String auctionStatus;

    public AuctionUpdateMessage() {
    }

    public AuctionUpdateMessage(AuctionUpdateType type, UUID itemId, String itemName, Double currentPrice, UUID highestBidder,
            String auctionStatus) {
        this.type = type;
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.auctionStatus = auctionStatus;
    }

    public AuctionUpdateType getType() {
        return type;
    }

    public void setType(AuctionUpdateType type) {
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

    public UUID getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(UUID highestBidder) {
        this.highestBidder = highestBidder;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }
}