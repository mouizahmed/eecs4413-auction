package com.teamAgile.backend.models;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "auctionType", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(10)")
@Table(name = "auctionItems")
public abstract class AuctionItem {
    
    public enum AuctionType {
        FORWARD, DUTCH
    }

    public enum AuctionStatus {
        AVAILABLE, SOLD, EXPIRED, CANCELLED, PAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "itemid", nullable = false)
    private UUID itemID;

    @Column(name = "itemname", nullable = false, unique = true)
    private String itemName;

    @Column(name = "currentprice", nullable = false)
    private Double currentPrice;
    
    @Column(name = "shippingtime", nullable = false)
    private Integer shippingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "auctionType", nullable = false, insertable = false, updatable = false)
    private AuctionType auctionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "auctionstatus", nullable = false)
    private AuctionStatus auctionStatus;

    @Column(name = "sellerid", nullable = false)
    private UUID sellerID;

    @Column(name = "highestbidder")
    private String highestBidder;

    // Default no-argument constructor required by JPA
    protected AuctionItem() {
    }

    public AuctionItem(AuctionType auctionType) {
        this.auctionType = auctionType;
        this.auctionStatus = AuctionStatus.AVAILABLE;
    }

    public UUID getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }
    
    public Integer getShippingTime() {
    	return shippingTime;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public UUID getSellerID() {
        return sellerID;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public void setShippingTime(Integer shippingTime) {
    	this.shippingTime = shippingTime;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public void setSellerID(UUID sellerID) {
        this.sellerID = sellerID;
    }

    public void placeBid(double newBid, String username) {
        // Base behavior can be overridden by subclasses.
    }
}
