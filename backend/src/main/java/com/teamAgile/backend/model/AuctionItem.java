package com.teamAgile.backend.model;

import java.util.UUID;
import jakarta.persistence.*;

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
    @Column(name = "itemId", nullable = false)
    private UUID itemID;

    @Column(name = "itemName", nullable = false, unique = true)
    private String itemName;

    @Column(name = "currentPrice", nullable = false)
    private Double currentPrice;
    
    @Column(name = "shippingTime", nullable = false)
    private Integer shippingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "auctionType", nullable = false, insertable = false, updatable = false)
    private AuctionType auctionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "auctionStatus", nullable = false)
    private AuctionStatus auctionStatus = AuctionStatus.AVAILABLE;

    // Updated: seller is now a User entity
    @ManyToOne(optional = false)
    @JoinColumn(name = "sellerId", nullable = false)
    private User seller;

    // Updated: highestBidder is now a foreign key to a User entity.
    @ManyToOne(optional = true)
    @JoinColumn(name = "highestBidderId")
    private User highestBidder;

    protected AuctionItem() {
    }

    public AuctionItem(String itemName, User seller, AuctionType auctionType, AuctionStatus auctionStatus, Double currentPrice, Integer shippingTime) {
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.shippingTime = shippingTime;
        this.seller = seller;
        this.auctionStatus = auctionStatus;
        this.auctionType = auctionType;
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

    public User getSeller() {
        return seller;
    }

    public User getHighestBidder() {
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

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setHighestBidder(User highestBidder) {
        this.highestBidder = highestBidder;
    }

    /**
     * Base behavior for placing a bid.
     * Subclasses can override this method.
     */
    public void placeBid(Double bidAmount, User user) {
        // Base behavior can be overridden by subclasses.
    }
    
    /**
     * Processes payment for the auction item.
     * Only the winning bidder can make the payment.
     *
     * @param userID the ID of the user attempting to make the payment
     */
    public void makePayment(User user) {
        if (this.getHighestBidder() == null || !this.getHighestBidder().getUserID().equals(user.getUserID())) {
            throw new IllegalArgumentException("You must be the winning bidder to place a payment on this item.");
        } else if (!this.getAuctionStatus().equals(AuctionStatus.SOLD)) {
            throw new IllegalArgumentException("The auction is either over or still ongoing.");
        }
        this.setAuctionStatus(AuctionStatus.PAID);
    }
}
