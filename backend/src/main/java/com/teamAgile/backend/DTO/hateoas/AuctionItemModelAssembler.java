package com.teamAgile.backend.DTO.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.teamAgile.backend.DTO.AuctionItemResponseDTO;
import com.teamAgile.backend.controller.AuctionController;
import com.teamAgile.backend.controller.UserController;

@Component
public class AuctionItemModelAssembler
        implements RepresentationModelAssembler<AuctionItemResponseDTO, AuctionItemModel> {

    @Override
    public AuctionItemModel toModel(AuctionItemResponseDTO auctionItem) {
        AuctionItemModel itemModel = new AuctionItemModel(auctionItem);

        itemModel.add(
                linkTo(methodOn(AuctionController.class).getAuctionItemByID(auctionItem.getItemID())).withSelfRel());

        itemModel.add(
                linkTo(methodOn(AuctionController.class).getAllAuctionItems()).withRel("auctionItems"));

        itemModel.add(
                linkTo(methodOn(AuctionController.class).getBidsForItem(auctionItem.getItemID())).withRel("bids"));

        return itemModel;
    }
}