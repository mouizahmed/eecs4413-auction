package com.teamAgile.backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teamAgile.backend.model.Bid;

public class BidResponseDTO {
    private UUID bidID;
    private UUID itemID;
    private UUID userID;
    private String username;
    private Double bidAmount;
    private LocalDateTime timestamp;

    public BidResponseDTO() {
    }

    public static BidResponseDTO fromBid(Bid bid) {
        BidResponseDTO dto = new BidResponseDTO();
        dto.bidID = bid.getBidID();
        dto.itemID = bid.getItemID();

        if (bid.getUser() != null) {
            dto.userID = bid.getUser().getUserID();
            dto.username = bid.getUser().getUsername();
        }

        dto.bidAmount = bid.getBidAmount();
        dto.timestamp = bid.getTimestamp();

        return dto;
    }

    // Getters
    public UUID getBidID() {
        return bidID;
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

    public Double getBidAmount() {
        return bidAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}