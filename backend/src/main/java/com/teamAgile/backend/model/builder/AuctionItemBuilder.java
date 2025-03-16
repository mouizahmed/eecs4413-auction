package com.teamAgile.backend.model.builder;

import java.time.LocalDateTime;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.AuctionItem.AuctionType;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;

/**
 * Builder pattern implementation for creating auction items.
 * This builder supports creating both Forward and Dutch auction items
 * with a fluent interface.
 */
public class AuctionItemBuilder {
    // Required parameters
    private String itemName;
    private User seller;
    private AuctionItem.AuctionType auctionType;
    private Double currentPrice;
    private Integer shippingTime;

    // Optional parameters with default values
    private AuctionItem.AuctionStatus auctionStatus = AuctionStatus.AVAILABLE;

    // Type-specific parameters
    private LocalDateTime endTime; // For Forward Auction
    private Double reservePrice; // For Dutch Auction

    /**
     * Private constructor to enforce the use of static factory methods
     */
    private AuctionItemBuilder() {
    }

    /**
     * Static factory method to create a builder for Forward Auction items
     * 
     * @param itemName     the name of the item
     * @param seller       the user selling the item
     * @param currentPrice the starting price
     * @param shippingTime the shipping time in days
     * @return a new AuctionItemBuilder instance
     */
    public static AuctionItemBuilder forwardAuction(String itemName, User seller, Double currentPrice,
            Integer shippingTime) {
        AuctionItemBuilder builder = new AuctionItemBuilder();
        builder.itemName = itemName;
        builder.seller = seller;
        builder.auctionType = AuctionType.FORWARD;
        builder.currentPrice = currentPrice;
        builder.shippingTime = shippingTime;
        return builder;
    }

    /**
     * Static factory method to create a builder for Dutch Auction items
     * 
     * @param itemName     the name of the item
     * @param seller       the user selling the item
     * @param currentPrice the starting price
     * @param shippingTime the shipping time in days
     * @return a new AuctionItemBuilder instance
     */
    public static AuctionItemBuilder dutchAuction(String itemName, User seller, Double currentPrice,
            Integer shippingTime) {
        AuctionItemBuilder builder = new AuctionItemBuilder();
        builder.itemName = itemName;
        builder.seller = seller;
        builder.auctionType = AuctionType.DUTCH;
        builder.currentPrice = currentPrice;
        builder.shippingTime = shippingTime;
        return builder;
    }

    /**
     * Set the auction status
     * 
     * @param auctionStatus the status of the auction
     * @return this builder instance
     */
    public AuctionItemBuilder withStatus(AuctionItem.AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
        return this;
    }

    /**
     * Set the end time for a Forward Auction
     * 
     * @param endTime the end time of the auction
     * @return this builder instance
     * @throws IllegalStateException if the auction type is not FORWARD
     */
    public AuctionItemBuilder withEndTime(LocalDateTime endTime) {
        if (this.auctionType != AuctionType.FORWARD) {
            throw new IllegalStateException("End time can only be set for Forward Auctions");
        }
        this.endTime = endTime;
        return this;
    }

    /**
     * Set the reserve price for a Dutch Auction
     * 
     * @param reservePrice the reserve price of the auction
     * @return this builder instance
     * @throws IllegalStateException if the auction type is not DUTCH
     */
    public AuctionItemBuilder withReservePrice(Double reservePrice) {
        if (this.auctionType != AuctionType.DUTCH) {
            throw new IllegalStateException("Reserve price can only be set for Dutch Auctions");
        }
        this.reservePrice = reservePrice;
        return this;
    }

    /**
     * Build the auction item based on the configured parameters
     * 
     * @return a new AuctionItem instance
     * @throws IllegalStateException if required type-specific parameters are
     *                               missing
     */
    public AuctionItem build() {
        validateBuildParameters();

        if (auctionType == AuctionType.FORWARD) {
            return new ForwardAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, endTime);
        } else {
            return new DutchAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, reservePrice);
        }
    }

    /**
     * Validate that all required parameters are set before building
     * 
     * @throws IllegalStateException if any required parameters are missing
     */
    private void validateBuildParameters() {
        if (itemName == null || seller == null || currentPrice == null || shippingTime == null) {
            throw new IllegalStateException(
                    "Required parameters missing: itemName, seller, currentPrice, and shippingTime must be set");
        }

        if (auctionType == AuctionType.FORWARD && endTime == null) {
            throw new IllegalStateException("End time must be set for Forward Auctions");
        }

        if (auctionType == AuctionType.DUTCH && reservePrice == null) {
            throw new IllegalStateException("Reserve price must be set for Dutch Auctions");
        }
    }
}