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
    
    public void setAuctionType(AuctionType auctionType) {
		// TODO Auto-generated method stub
    	this.auctionType = auctionType;
		
	}
	
	
}
