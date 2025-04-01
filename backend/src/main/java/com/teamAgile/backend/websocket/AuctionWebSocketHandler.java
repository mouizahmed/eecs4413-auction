package com.teamAgile.backend.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.websocket.AuctionUpdateMessage.AuctionUpdateType;

@Component
public class AuctionWebSocketHandler extends TextWebSocketHandler {
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	private final Map<String, UUID> sessionSubscriptions = new ConcurrentHashMap<>();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final AuctionRepository auctionRepository;

	@Autowired
	public AuctionWebSocketHandler(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.put(session.getId(), session);

		sendMessage(session, new TextMessage("{\"type\":\"CONNECTED\",\"message\":\"Connected to auction updates\"}"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session.getId());
		sessionSubscriptions.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
			String type = (String) payload.get("type");

			if ("SUBSCRIBE".equals(type)) {
				String itemIdStr = (String) payload.get("itemId");
				if (itemIdStr != null) {
					UUID itemId = UUID.fromString(itemIdStr);

					Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(itemId);
					if (itemOptional.isEmpty()) {
						throw new IllegalArgumentException("Auction item with ID " + itemId + " does not exist");
					}

					sessionSubscriptions.put(session.getId(), itemId);
					sendMessage(session, new TextMessage("{\"type\":\"SUBSCRIBED\",\"itemId\":\"" + itemId + "\"}"));
				} else {
					throw new IllegalArgumentException("You must provide an item id");
				}
			} else {
				throw new IllegalArgumentException("Not a valid message type.");
			}
		} catch (Exception e) {
			sendMessage(session, new TextMessage("{\"type\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}"));
		}
	}

	public void broadcastNewPayment(Receipt receipt) {
		try {
			Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(receipt.getItemID());
			if (itemOptional.isEmpty()) {
				System.err.println("Cannot broadcast payment for non-existent item: " + receipt.getItemID());
				return;
			}

			Map<String, Object> paymentUpdateMessage = new HashMap<>();
			paymentUpdateMessage.put("type", "PAYMENT_MADE");
			paymentUpdateMessage.put("itemId", receipt.getItemID().toString());
			paymentUpdateMessage.put("userId", receipt.getUser().getUserID().toString());

			String messageJson = objectMapper.writeValueAsString(paymentUpdateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				if (subscribedItemId == null || subscribedItemId.equals(receipt.getItemID())) {
					if (session.isOpen()) {
						try {
							session.sendMessage(textMessage);
						} catch (IOException e) {
							System.err.println(
									"Error sending bid update to session " + sessionId + ": " + e.getMessage());
						}
					}
				}
			});

		} catch (Exception e) {
			System.err.println("Error broadcasting new payment update: " + e.getMessage());
		}
	}

	public void broadcastNewBid(Bid bid) {
		try {
			Optional<AuctionItem> itemOptional = auctionRepository.findByItemID(bid.getItemID());
			if (itemOptional.isEmpty()) {
				System.err.println("Cannot broadcast bid for non-existent item: " + bid.getItemID());
				return;
			}

			AuctionItem item = itemOptional.get();

			Map<String, Object> bidUpdateMessage = new HashMap<>();
			bidUpdateMessage.put("type", "BID_PLACED");
			bidUpdateMessage.put("itemId", bid.getItemID().toString());
			bidUpdateMessage.put("bidId", bid.getBidID().toString());
			bidUpdateMessage.put("userId", bid.getUser().getUserID().toString());
			bidUpdateMessage.put("username", bid.getUser().getUsername());
			bidUpdateMessage.put("bidAmount", bid.getBidAmount());
			bidUpdateMessage.put("itemName", item.getItemName());
			bidUpdateMessage.put("currentPrice", item.getCurrentPrice());
			bidUpdateMessage.put("timestamp", bid.getTimestamp().toString());

			String messageJson = objectMapper.writeValueAsString(bidUpdateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				if (subscribedItemId == null || subscribedItemId.equals(bid.getItemID())) {
					if (session.isOpen()) {
						try {
							session.sendMessage(textMessage);
						} catch (IOException e) {
							System.err.println(
									"Error sending bid update to session " + sessionId + ": " + e.getMessage());
						}
					}
				}
			});
		} catch (Exception e) {
			System.err.println("Error broadcasting new bid update: " + e.getMessage());
		}
	}

	public void broadcastAuctionUpdate(AuctionItem auctionItem) {
		try {
			UUID highestBidderID = auctionItem.getHighestBidder() != null ? auctionItem.getHighestBidder().getUserID()
					: null;
			String highestBidderUsername = auctionItem.getHighestBidder() != null
					? auctionItem.getHighestBidder().getUsername()
					: null;

			AuctionUpdateMessage updateMessage = new AuctionUpdateMessage(AuctionUpdateType.AUCTION_UPDATE,
					auctionItem.getItemID(), auctionItem.getItemName(), auctionItem.getCurrentPrice(),
					highestBidderID, highestBidderUsername, auctionItem.getAuctionStatus().toString());

			String messageJson = objectMapper.writeValueAsString(updateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				if (subscribedItemId == null || subscribedItemId.equals(auctionItem.getItemID())) {
					if (session.isOpen()) {
						try {
							session.sendMessage(textMessage);
						} catch (IOException e) {
							System.err.println("Error sending message to session " + sessionId + ": " + e.getMessage());
						}
					}
				}
			});
		} catch (Exception e) {
			System.err.println("Error broadcasting auction update: " + e.getMessage());
		}
	}

	private void sendMessage(WebSocketSession session, TextMessage message) {
		try {
			if (session.isOpen()) {
				session.sendMessage(message);
			}
		} catch (IOException e) {
			System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
		}
	}
}