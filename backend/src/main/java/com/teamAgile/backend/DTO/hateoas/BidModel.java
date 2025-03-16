package com.teamAgile.backend.DTO.hateoas;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.teamAgile.backend.DTO.BidResponseDTO;

@Relation(collectionRelation = "bids", itemRelation = "bid")
public class BidModel extends RepresentationModel<BidModel> {

    private final BidResponseDTO bid;

    public BidModel(BidResponseDTO bid) {
        this.bid = bid;
    }

    public BidResponseDTO getBid() {
        return bid;
    }
}