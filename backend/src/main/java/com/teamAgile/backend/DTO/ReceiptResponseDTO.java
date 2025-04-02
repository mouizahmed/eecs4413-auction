package com.teamAgile.backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.CreditCard;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.AuctionItem;

public class ReceiptResponseDTO {
    private UUID receiptID;
    private UUID itemID;
    private UUID userID;
    private String username;
    private UUID sellerID;
    private String sellerUsername;
    private Double totalCost;
    private CreditCard creditCard;
    private Address address;
    private Integer shippingTime;
    private LocalDateTime timestamp;

    public ReceiptResponseDTO() {
    }

    public static ReceiptResponseDTO fromReceipt(Receipt receipt) {
        ReceiptResponseDTO dto = new ReceiptResponseDTO();
        dto.receiptID = receipt.getReceiptID();
        dto.itemID = receipt.getItemID();
        dto.totalCost = receipt.getTotalCost();
        dto.creditCard = receipt.getCreditCard();
        dto.address = receipt.getAddress();
        dto.shippingTime = receipt.getShippingTime();
        dto.timestamp = receipt.getTimestamp();

        if (receipt.getUser() != null) {
            dto.userID = receipt.getUser().getUserID();
            dto.username = receipt.getUser().getUsername();
        }

        // Get seller information from the auction item
        AuctionItem item = receipt.getAuctionItem();
        if (item != null && item.getSeller() != null) {
            dto.sellerID = item.getSeller().getUserID();
            dto.sellerUsername = item.getSeller().getUsername();
        }

        return dto;
    }

    // Getters
    public UUID getReceiptID() {
        return receiptID;
    }

    public UUID getItemID() {
        return itemID;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public Address getAddress() {
        return address;
    }

    public Integer getShippingTime() {
        return shippingTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public UUID getSellerID() {
        return sellerID;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    // Setters
    public void setReceiptID(UUID receiptID) {
        this.receiptID = receiptID;
    }

    public void setItemID(UUID itemID) {
        this.itemID = itemID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setShippingTime(Integer shippingTime) {
        this.shippingTime = shippingTime;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSellerID(UUID sellerID) {
        this.sellerID = sellerID;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
}