package com.teamAgile.backend.DTO.hateoas;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.teamAgile.backend.DTO.AuctionItemResponseDTO;

@Relation(collectionRelation = "auctionItems", itemRelation = "auctionItem")
public class AuctionItemModel extends RepresentationModel<AuctionItemModel> {

    private final AuctionItemResponseDTO auctionItem;

    public AuctionItemModel(AuctionItemResponseDTO auctionItem) {
        this.auctionItem = auctionItem;
    }

    public AuctionItemResponseDTO getAuctionItem() {
        return auctionItem;
    }
}