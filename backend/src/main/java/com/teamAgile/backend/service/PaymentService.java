package com.teamAgile.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.DTO.CreditCardDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.ReceiptRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@Service
public class PaymentService {

    private final AuctionRepository auctionRepository;
    private final ReceiptRepository receiptRepository;
    private final AuctionWebSocketHandler auctionWebSocketHandler;

    @Autowired
    public PaymentService(AuctionRepository auctionRepository, ReceiptRepository receiptRepository, AuctionWebSocketHandler auctionWebSocketHandler) {
        this.auctionRepository = auctionRepository;
        this.receiptRepository = receiptRepository;
        this.auctionWebSocketHandler = auctionWebSocketHandler;
    }

    public Receipt createPayment(UUID itemID, User user, CreditCardDTO cardDetails) {

        Optional<AuctionItem> itemOptional = auctionRepository.findById(itemID);
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("Auction item not found");
        }

        AuctionItem item = itemOptional.get();
        LocalDateTime now = LocalDateTime.now();

        // check if user is the winning bidder
        if (!item.getHighestBidder().equals(user.getUserID())) {
            throw new IllegalArgumentException("You must be the winning bidder to place a payment on this item.");
        }

        if (!item.getAuctionStatus().equals(AuctionStatus.SOLD)) {
            throw new IllegalArgumentException("The auction is not over yet!");
        }

        Receipt receipt = new Receipt();
        receipt.setItemID(itemID);
        receipt.setUserID(user.getUserID());
        receipt.setTotalCost(item.getCurrentPrice());
        receipt.setCardNum(cardDetails.getCardNum());
        receipt.setCardName(cardDetails.getCardName());
        receipt.setExpDate(cardDetails.getExpDate());
        receipt.setSecurityCode(cardDetails.getSecurityCode());
        receipt.setStreetName(user.getStreetName());
        receipt.setStreetNumber(user.getStreetNumber());
        receipt.setPostalCode(user.getPostalCode());
        receipt.setCountry(user.getCountry());
        receipt.setShippingTime(item.getShippingTime());
        
        item.makePayment(user.getUserID());
        auctionRepository.save(item);
        receiptRepository.save(receipt);
        
        auctionWebSocketHandler.broadcastAuctionUpdate(item);
        auctionWebSocketHandler.broadcastNewPayment(receipt);
        
        return receipt;

    }

}
