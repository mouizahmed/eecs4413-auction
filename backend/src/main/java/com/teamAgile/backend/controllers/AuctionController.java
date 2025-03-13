package com.teamAgile.backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.models.AuctionItem;
import com.teamAgile.backend.models.DutchAuctionItem;
import com.teamAgile.backend.models.ForwardAuctionItem;
import com.teamAgile.backend.models.Bid;
import com.teamAgile.backend.models.CreditCardDTO;
import com.teamAgile.backend.services.AuctionService;
import com.teamAgile.backend.services.BidService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auction")
public class AuctionController extends BaseController {

	private final AuctionService auctionService;
	private final BidService bidService;

	public AuctionController(AuctionService auctionService, BidService bidService) {
		this.auctionService = auctionService;
		this.bidService = bidService;
	}

	@GetMapping("/get-all")
	public ResponseEntity<List<AuctionItem>> getAllUsers(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> sessionUser = (Map<String, Object>) session.getAttribute("user");
		if (sessionUser == null || !sessionUser.containsKey("userId") || !sessionUser.containsKey("username")) {
			session.invalidate();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		List<AuctionItem> auctionItems = auctionService.getAllAuctionItems();
		return ResponseEntity.ok(auctionItems);
	}

	@GetMapping("/item")
	public ResponseEntity<AuctionItem> getAuctionItem(@RequestParam String name) {
		System.out.println(name);
		AuctionItem item = auctionService.getAuctionItemByName(name);
		if (item == null) {
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.ok(item);
	}

	@PostMapping("/forward/post")
	public ResponseEntity<?> uploadForwardAuctionItem(@RequestBody ForwardAuctionItem auctionItem, HttpServletRequest request) {
		Map<String, Object> currentUser = getCurrentUser(request);

		try {
			UUID userID = (UUID) currentUser.get("userID");
			if (userID == null)
				throw new IllegalArgumentException("No UserID");

			AuctionItem item = auctionService.createForwardItem(auctionItem, userID);

			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/dutch/post")
	public ResponseEntity<?> uploadDutchAuctionItem(@RequestBody DutchAuctionItem auctionItem,
			HttpServletRequest request) {

		Map<String, Object> currentUser = getCurrentUser(request);

		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.get("userID");
			if (userID == null)
				throw new IllegalArgumentException("No UserID");

			AuctionItem item = auctionService.createDutchItem(auctionItem, userID);

			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}

	}
	
	@PostMapping("/forward/{itemID}/bid")
	public ResponseEntity<?> placeForwardBid(@PathVariable UUID itemID, @RequestBody Map<String, Double> bidRequest,
			HttpServletRequest request) {
		Map<String, Object> currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.get("userID");
			Double bidPrice = bidRequest.get("bidPrice");

			if (bidPrice == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid price is required");
			}

			Bid bid = bidService.createForwardBid(itemID, userID, bidPrice);
			return ResponseEntity.ok(bid);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/dutch/{itemID}/bid")
	public ResponseEntity<?> placeDutchBid(@PathVariable UUID itemID, @RequestBody Map<String, Double> bidRequest,
			HttpServletRequest request) {
		Map<String, Object> currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.get("userID");
			Double bidPrice = bidRequest.get("bidPrice");

			if (bidPrice == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid price is required");
			}

			Bid bid = bidService.createDutchBid(itemID, userID, bidPrice);
			return ResponseEntity.ok(bid);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PatchMapping("/dutch/{itemID}/decreasePrice")
	public ResponseEntity<?> decreaseDutchPrice(@PathVariable UUID itemID,
			@RequestBody Map<String, Double> decreasePriceRequest, HttpServletRequest request) {

		Map<String, Object> currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.get("userID");
			Double decreaseBy = decreasePriceRequest.get("decreaseBy");
			AuctionItem item = auctionService.decreaseDutchPrice(itemID, userID, decreaseBy);
			return ResponseEntity.ok(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/pay/{itemID}")
	public ResponseEntity<?> processPayment(@PathVariable UUID itemID, @RequestBody CreditCardDTO creditCardDetails,
			HttpServletRequest request) {

		Map<String, Object> currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			System.out.println(creditCardDetails.getCardNumber());
			return null;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}

	}

}
