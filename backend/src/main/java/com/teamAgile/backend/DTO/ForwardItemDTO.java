package com.teamAgile.backend.DTO;

import java.time.LocalDateTime;

import com.teamAgile.backend.model.AuctionItem.AuctionType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public class ForwardItemDTO extends AuctionItemDTO {

	@NotNull(message = "Event time is required")
	@Future(message = "Event time must be in the future")
	private LocalDateTime endTime;

	public ForwardItemDTO() {
		super();
		setAuctionType(AuctionType.FORWARD);
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

}
