package com.teamAgile.backend.DTO;

import com.teamAgile.backend.model.AuctionItem.AuctionType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public class DutchItemDTO extends AuctionItemDTO {

	@NotNull(message = "reservePrice is required")
	private Double reservePrice;

	public DutchItemDTO() {
		// Call the parent constructor (optional if no-arg)
		super();
		// Set auctionType to FORWARD here
		setAuctionType(AuctionType.DUTCH);
	}

	public Double getReservePrice() {
		return reservePrice;
	}

}
