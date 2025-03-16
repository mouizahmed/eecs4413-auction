package com.teamAgile.backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teamAgile.backend.model.Receipt;

public class ReceiptResponseDTO {
    private UUID receiptID;
    private UUID itemID;
    private UUID userID;
    private String username;
    private Double totalCost;
    private Integer shippingTime;
    private LocalDateTime timestamp;

    public ReceiptResponseDTO() {
    }

    public static ReceiptResponseDTO fromReceipt(Receipt receipt) {
        ReceiptResponseDTO dto = new ReceiptResponseDTO();
        dto.receiptID = receipt.getReceiptID();
        dto.itemID = receipt.getItemID();
        dto.totalCost = receipt.getTotalCost();
        dto.shippingTime = receipt.getShippingTime();
        dto.timestamp = receipt.getTimestamp();

        if (receipt.getUser() != null) {
            dto.userID = receipt.getUser().getUserID();
            dto.username = receipt.getUser().getUsername();
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

    public Integer getShippingTime() {
        return shippingTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}