package com.teamAgile.backend.model.builder;

import java.time.LocalDateTime;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.AuctionItem.AuctionType;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;

public class AuctionItemBuilder {
    private String itemName;
    private User seller;
    private AuctionItem.AuctionType auctionType;
    private Double currentPrice;
    private Integer shippingTime;

    private AuctionItem.AuctionStatus auctionStatus = AuctionStatus.AVAILABLE;

    private LocalDateTime endTime; // For Forward Auction
    private Double reservePrice; // For Dutch Auction

    private AuctionItemBuilder() {
    }

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

    public AuctionItemBuilder withStatus(AuctionItem.AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
        return this;
    }


    public AuctionItemBuilder withEndTime(LocalDateTime endTime) {
        if (this.auctionType != AuctionType.FORWARD) {
            throw new IllegalStateException("End time can only be set for Forward Auctions");
        }
        this.endTime = endTime;
        return this;
    }

    public AuctionItemBuilder withReservePrice(Double reservePrice) {
        if (this.auctionType != AuctionType.DUTCH) {
            throw new IllegalStateException("Reserve price can only be set for Dutch Auctions");
        }
        this.reservePrice = reservePrice;
        return this;
    }

    public AuctionItem build() {
        validateBuildParameters();

        if (auctionType == AuctionType.FORWARD) {
            return new ForwardAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, endTime);
        } else {
            return new DutchAuctionItem(itemName, seller, auctionStatus, currentPrice, shippingTime, reservePrice);
        }
    }

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