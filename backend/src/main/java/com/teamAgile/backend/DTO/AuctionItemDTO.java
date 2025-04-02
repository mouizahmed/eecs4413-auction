package com.teamAgile.backend.DTO;

import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.AuctionItem.AuctionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuctionItemDTO {

    @NotBlank(message = "itemName is required")
    private String itemName;

    @NotNull(message = "currentPrice is required")
    private Double currentPrice;

    @NotNull(message = "shippingTime is required")
    private Integer shippingTime;

    private AuctionType auctionType;

    private AuctionStatus auctionStatus = AuctionStatus.AVAILABLE;

    public AuctionItemDTO() {
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

    public Integer getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(Integer shippingTime) {
        this.shippingTime = shippingTime;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
    }
}
