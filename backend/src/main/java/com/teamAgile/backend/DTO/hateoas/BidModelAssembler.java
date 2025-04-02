package com.teamAgile.backend.DTO.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.teamAgile.backend.DTO.BidResponseDTO;
import com.teamAgile.backend.controller.AuctionController;
import com.teamAgile.backend.controller.UserController;

@Component
public class BidModelAssembler implements RepresentationModelAssembler<BidResponseDTO, BidModel> {

    @Override
    public BidModel toModel(BidResponseDTO bid) {
        BidModel bidModel = new BidModel(bid);

        bidModel.add(
                linkTo(methodOn(AuctionController.class).getAuctionItemByID(bid.getItemID())).withRel("auctionItem"));

        bidModel.add(
                linkTo(methodOn(AuctionController.class).getBidsForItem(bid.getItemID())).withRel("itemBids"));

        return bidModel;
    }
}