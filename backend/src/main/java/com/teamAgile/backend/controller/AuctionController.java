package com.teamAgile.backend.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.DTO.CreditCardDTO;
import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.BidService;
import com.teamAgile.backend.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auction")
public class AuctionController extends BaseController {

	private final AuctionService auctionService;
	private final BidService bidService;
	private final PaymentService paymentService;

	public AuctionController(AuctionService auctionService, BidService bidService, PaymentService paymentService) {
		this.auctionService = auctionService;
		this.bidService = bidService;
		this.paymentService = paymentService;
	}

	@GetMapping("/get-all")
	public ResponseEntity<?> getAllUsers() {
		try {
			List<AuctionItem> auctionItems = auctionService.getAllAuctionItems();
			return ResponseEntity.ok(auctionItems);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getAuctionItem(@RequestParam("keyword") String keyword) {
		try {
			List<AuctionItem> items = auctionService.searchByKeyword(keyword);
			return ResponseEntity.ok(items);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/forward/post")
	public ResponseEntity<?> uploadForwardAuctionItem(@Valid @RequestBody ForwardItemDTO forwardItemDTO,
			HttpServletRequest request) {
		User currentUser = getCurrentUser(request);

		try {
			UUID userID = (UUID) currentUser.getUserID();
			if (userID == null)
				throw new IllegalArgumentException("No UserID");

			AuctionItem item = auctionService.createForwardItem(forwardItemDTO, userID);

			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/dutch/post")
	public ResponseEntity<?> uploadDutchAuctionItem(@Valid @RequestBody DutchItemDTO dutchItemDTO,
			HttpServletRequest request) {

		User currentUser = getCurrentUser(request);

		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.getUserID();
			if (userID == null)
				throw new IllegalArgumentException("No UserID");

			AuctionItem item = auctionService.createDutchItem(dutchItemDTO, userID);
			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/forward/{itemID}/bid")
	public ResponseEntity<?> placeForwardBid(@PathVariable UUID itemID, @RequestBody Map<String, Double> bidRequest,
			HttpServletRequest request) {
		User currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.getUserID();
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
		User currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.getUserID();
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

		User currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			UUID userID = (UUID) currentUser.getUserID();
			Double decreaseBy = decreasePriceRequest.get("decreaseBy");
			AuctionItem item = auctionService.decreaseDutchPrice(itemID, userID, decreaseBy);
			return ResponseEntity.ok(item);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/pay/{itemID}")
	public ResponseEntity<?> processPayment(@PathVariable UUID itemID, @RequestBody CreditCardDTO cardDetails,
			BindingResult bindingResult, HttpServletRequest request) {

		User currentUser = getCurrentUser(request);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Check for validation errors from the DTO annotations
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
		}

		// Additional business validation: e.g., check if the expiration date is in the
		// future
		if (cardDetails.getExpDate().isBefore(YearMonth.now())) {
			return ResponseEntity.badRequest().body("Expiration date cannot be in the past");
		}

		try {
			User user = (User) currentUser;
			Receipt receipt = paymentService.createPayment(itemID, user, cardDetails);
			return ResponseEntity.ok(receipt);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}

	}

}
