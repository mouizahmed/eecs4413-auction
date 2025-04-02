package com.teamAgile.backend.DTO;

import com.teamAgile.backend.model.AuctionItem.AuctionType;
import jakarta.validation.constraints.NotNull;

public class DutchItemDTO extends AuctionItemDTO {

	@NotNull(message = "reservePrice is required")
	private Double reservePrice;

	public DutchItemDTO() {
		super();
		setAuctionType(AuctionType.DUTCH);
	}

	public Double getReservePrice() {
		return reservePrice;
	}

	public void setReservePrice(Double reservePrice) {
		this.reservePrice = reservePrice;
	}

}
