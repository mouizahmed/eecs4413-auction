package com.teamAgile.backend.DTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.AuctionItem.AuctionType;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;

public class AuctionItemResponseDTO {
    private UUID itemID;
    private String itemName;
    private Double currentPrice;
    private Integer shippingTime;
    private AuctionType auctionType;
    private AuctionStatus auctionStatus;

    private UUID sellerID;
    private String sellerUsername;

    private UUID highestBidderID;
    private String highestBidderUsername;

    private java.time.LocalDateTime endTime;

    private Double reservePrice;

    private List<BidResponseDTO> bids;

    public AuctionItemResponseDTO() {
    }

    public static AuctionItemResponseDTO fromAuctionItem(AuctionItem item) {
        return fromAuctionItem(item, null);
    }

    public static AuctionItemResponseDTO fromAuctionItem(AuctionItem item, List<Bid> bids) {
        AuctionItemResponseDTO dto = new AuctionItemResponseDTO();
        dto.itemID = item.getItemID();
        dto.itemName = item.getItemName();
        dto.currentPrice = item.getCurrentPrice();
        dto.shippingTime = item.getShippingTime();
        dto.auctionType = item.getAuctionType();
        dto.auctionStatus = item.getAuctionStatus();

        if (item.getSeller() != null) {
            dto.sellerID = item.getSeller().getUserID();
            dto.sellerUsername = item.getSeller().getUsername();
        }

        if (item.getHighestBidder() != null) {
            dto.highestBidderID = item.getHighestBidder().getUserID();
            dto.highestBidderUsername = item.getHighestBidder().getUsername();
        }

        if (item instanceof ForwardAuctionItem) {
            ForwardAuctionItem forwardAuction = (ForwardAuctionItem) item;
            dto.endTime = forwardAuction.getEndTime();
        } else if (item instanceof DutchAuctionItem) {
            DutchAuctionItem dutchAuction = (DutchAuctionItem) item;
            dto.reservePrice = dutchAuction.getReservePrice();
        }

        if (bids != null) {
            dto.bids = bids.stream()
                    .map(BidResponseDTO::fromBid)
                    .collect(Collectors.toList());
        }

        return dto;
    }

	// Getters
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

    public String getSellerUsername() {
        return sellerUsername;
    }

    public UUID getHighestBidderID() {
        return highestBidderID;
    }

    public String getHighestBidderUsername() {
        return highestBidderUsername;
    }

    public java.time.LocalDateTime getEndTime() {
        return endTime;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public List<BidResponseDTO> getBids() {
        return bids;
    }
}