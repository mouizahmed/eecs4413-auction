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
	// Store all active WebSocket sessions
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	// Map to track which auction items each session is subscribed to
	private final Map<String, UUID> sessionSubscriptions = new ConcurrentHashMap<>();

	private final ObjectMapper objectMapper = new ObjectMapper();

	// Add repository for item validation
	private final AuctionRepository auctionRepository;

	@Autowired
	public AuctionWebSocketHandler(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// Add the session to our sessions map
		sessions.put(session.getId(), session);

		// Send a connection confirmation message
		sendMessage(session, new TextMessage("{\"type\":\"CONNECTED\",\"message\":\"Connected to auction updates\"}"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// Remove the session from our maps when the connection is closed
		sessions.remove(session.getId());
		sessionSubscriptions.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			// Parse the incoming message
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
			String type = (String) payload.get("type");

			if ("SUBSCRIBE".equals(type)) {
				// Handle subscription to an auction item
				String itemIdStr = (String) payload.get("itemId");
				if (itemIdStr != null) {
					UUID itemId = UUID.fromString(itemIdStr);

					// Validate that the item exists in the database
					Optional<AuctionItem> itemOptional = auctionRepository.findById(itemId);
					if (itemOptional.isEmpty()) {
						throw new IllegalArgumentException("Auction item with ID " + itemId + " does not exist");
					}

					// Item exists, proceed with subscription
					sessionSubscriptions.put(session.getId(), itemId);
					sendMessage(session, new TextMessage("{\"type\":\"SUBSCRIBED\",\"itemId\":\"" + itemId + "\"}"));
				} else {
					throw new IllegalArgumentException("You must provide an item id");
				}
			} else {
				throw new IllegalArgumentException("Not a valid message type.");
			}
		} catch (Exception e) {
			// Send error message back to client
			sendMessage(session, new TextMessage("{\"type\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}"));
		}
	}

	public void broadcastNewPayment(Receipt receipt) {
		try {
			// Fetch the auction item to include its details
			Optional<AuctionItem> itemOptional = auctionRepository.findById(receipt.getItemID());
			if (itemOptional.isEmpty()) {
				System.err.println("Cannot broadcast payment for non-existent item: " + receipt.getItemID());
				return;
			}

			AuctionItem item = itemOptional.get();

			Map<String, Object> paymentUpdateMessage = new HashMap<>();
			paymentUpdateMessage.put("type", "PAYMENT_MADE");
			paymentUpdateMessage.put("itemId", receipt.getItemID().toString());
			paymentUpdateMessage.put("userId", receipt.getUserID().toString());

			String messageJson = objectMapper.writeValueAsString(paymentUpdateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			// Send to all connected sessions that are subscribed to this item
			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				// Send if the session is subscribed to this specific item or has no
				// subscription
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
			// Fetch the auction item to include its details
			Optional<AuctionItem> itemOptional = auctionRepository.findById(bid.getItemID());
			if (itemOptional.isEmpty()) {
				System.err.println("Cannot broadcast bid for non-existent item: " + bid.getItemID());
				return;
			}

			AuctionItem item = itemOptional.get();

			// Create a bid update message
			Map<String, Object> bidUpdateMessage = new HashMap<>();
			bidUpdateMessage.put("type", "BID_PLACED");
			bidUpdateMessage.put("itemId", bid.getItemID().toString());
			bidUpdateMessage.put("bidId", bid.getBidID().toString());
			bidUpdateMessage.put("userId", bid.getUserID().toString());
			bidUpdateMessage.put("bidPrice", bid.getBidPrice());
			// bidUpdateMessage.put("timestamp", bid.getTimestamp().toString());
			bidUpdateMessage.put("itemName", item.getItemName());
			bidUpdateMessage.put("currentPrice", item.getCurrentPrice());

			String messageJson = objectMapper.writeValueAsString(bidUpdateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			// Send to all connected sessions that are subscribed to this item
			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				// Send if the session is subscribed to this specific item or has no
				// subscription
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

	/**
	 * Broadcasts an auction update to all connected clients who are subscribed to
	 * the specific item
	 * 
	 * @param auctionItem The auction item that was updated
	 */
	public void broadcastAuctionUpdate(AuctionItem auctionItem) {
		try {
			// Create the update message
			AuctionUpdateMessage updateMessage = new AuctionUpdateMessage(AuctionUpdateType.AUCTION_UPDATE,
					auctionItem.getItemID(), auctionItem.getItemName(), auctionItem.getCurrentPrice(),
					auctionItem.getHighestBidder(), auctionItem.getAuctionStatus().toString());

			String messageJson = objectMapper.writeValueAsString(updateMessage);
			TextMessage textMessage = new TextMessage(messageJson);

			// Send to all connected sessions that are subscribed to this item or have no
			// specific subscription
			sessions.forEach((sessionId, session) -> {
				UUID subscribedItemId = sessionSubscriptions.get(sessionId);

				// Send if the session is subscribed to this specific item or has no
				// subscription (gets all updates)
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

	/**
	 * Sends a message to a specific WebSocket session
	 */
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