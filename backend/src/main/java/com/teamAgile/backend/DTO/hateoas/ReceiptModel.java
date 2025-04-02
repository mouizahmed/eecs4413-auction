package com.teamAgile.backend.DTO.hateoas;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.teamAgile.backend.DTO.ReceiptResponseDTO;

@Relation(collectionRelation = "receipts", itemRelation = "receipt")
public class ReceiptModel extends RepresentationModel<ReceiptModel> {

    private final ReceiptResponseDTO receipt;

    public ReceiptModel(ReceiptResponseDTO receipt) {
        this.receipt = receipt;
    }

    public ReceiptResponseDTO getReceipt() {
        return receipt;
    }
}