package com.teamAgile.backend.DTO.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.teamAgile.backend.DTO.ReceiptResponseDTO;
import com.teamAgile.backend.controller.AuctionController;

@Component
public class ReceiptModelAssembler implements RepresentationModelAssembler<ReceiptResponseDTO, ReceiptModel> {

    @Override
    public ReceiptModel toModel(ReceiptResponseDTO receipt) {
        ReceiptModel receiptModel = new ReceiptModel(receipt);

        receiptModel.add(
                linkTo(methodOn(AuctionController.class).getReceiptById(receipt.getReceiptID().toString(), null))
                        .withSelfRel());

        receiptModel.add(
                linkTo(methodOn(AuctionController.class).getAuctionItemById(receipt.getItemID()))
                        .withRel("auctionItem"));

        return receiptModel;
    }
}