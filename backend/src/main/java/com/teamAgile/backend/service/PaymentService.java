package com.teamAgile.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamAgile.backend.DTO.CreditCardDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.CreditCard;
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
	public PaymentService(AuctionRepository auctionRepository, ReceiptRepository receiptRepository,
			AuctionWebSocketHandler auctionWebSocketHandler) {
		this.auctionRepository = auctionRepository;
		this.receiptRepository = receiptRepository;
		this.auctionWebSocketHandler = auctionWebSocketHandler;
	}

	public Receipt createPayment(UUID itemID, User user, CreditCardDTO cardDetails) {

		Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemID);
		if (itemOptional.isEmpty()) {
			throw new IllegalArgumentException("Auction item not found");
		}

		AuctionItem item = itemOptional.get();

		CreditCard creditCard = new CreditCard(cardDetails.getCardNum(), cardDetails.getCardName(),
				cardDetails.getExpDate(), cardDetails.getSecurityCode());
		Receipt receipt = new Receipt(itemID, user, item.getCurrentPrice(), creditCard, user.getAddress(),
				item.getShippingTime());
		item.makePayment(user);

		auctionRepository.save(item);
		receiptRepository.save(receipt);

		auctionWebSocketHandler.broadcastAuctionUpdate(item);
		auctionWebSocketHandler.broadcastNewPayment(receipt);

		return receipt;

	}

	public Receipt getReceiptById(UUID receiptId) {
		Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);
		if (receiptOptional.isEmpty()) {
			throw new IllegalArgumentException("Receipt not found");
		}
		return receiptOptional.get();
	}

	public List<Receipt> getReceiptsByUserId(UUID userId) {
		return receiptRepository.findByUser_UserID(userId);
	}

}
