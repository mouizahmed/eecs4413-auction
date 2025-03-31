package com.teamAgile.backend.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.teamAgile.backend.DTO.ApiResponse;
import com.teamAgile.backend.DTO.AuctionItemResponseDTO;
import com.teamAgile.backend.DTO.BidResponseDTO;
import com.teamAgile.backend.DTO.CreditCardDTO;
import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.DTO.ReceiptResponseDTO;
import com.teamAgile.backend.DTO.hateoas.AuctionItemModel;
import com.teamAgile.backend.DTO.hateoas.AuctionItemModelAssembler;
import com.teamAgile.backend.DTO.hateoas.BidModel;
import com.teamAgile.backend.DTO.hateoas.BidModelAssembler;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.BidService;
import com.teamAgile.backend.service.PaymentService;
import com.teamAgile.backend.util.CreditCardValidator;
import com.teamAgile.backend.util.ResponseUtil;
import com.teamAgile.backend.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/auction")
public class AuctionController extends BaseController {

	private final AuctionService auctionService;
	private final BidService bidService;
	private final PaymentService paymentService;
	private final AuctionItemModelAssembler auctionItemModelAssembler;
	private final BidModelAssembler bidModelAssembler;

	public AuctionController(AuctionService auctionService, BidService bidService, PaymentService paymentService,
			AuctionItemModelAssembler auctionItemModelAssembler, BidModelAssembler bidModelAssembler) {
		this.auctionService = auctionService;
		this.bidService = bidService;
		this.paymentService = paymentService;
		this.auctionItemModelAssembler = auctionItemModelAssembler;
		this.bidModelAssembler = bidModelAssembler;
	}

	@GetMapping("/get-all")
	public ResponseEntity<ApiResponse<CollectionModel<AuctionItemModel>>> getAllAuctionItems() {
		try {
			List<AuctionItem> auctionItems = auctionService.getAvailableAuctionItems();

			List<AuctionItemResponseDTO> responseItems = auctionItems.stream()
					.map(AuctionItemResponseDTO::fromAuctionItem).collect(Collectors.toList());

			List<AuctionItemModel> itemModels = responseItems.stream().map(auctionItemModelAssembler::toModel)
					.collect(Collectors.toList());

			CollectionModel<AuctionItemModel> collectionModel = CollectionModel.of(itemModels,
					linkTo(methodOn(AuctionController.class).getAllAuctionItems()).withSelfRel());

			return ResponseUtil.ok(collectionModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving auction items: " + e.getMessage());
		}
	}

	@GetMapping("/admin/get-all-items")
	public ResponseEntity<ApiResponse<CollectionModel<AuctionItemModel>>> getAllAuctionItemsAdmin() {
		try {
			List<AuctionItem> auctionItems = auctionService.getAllAuctionItems();

			List<AuctionItemResponseDTO> responseItems = auctionItems.stream()
					.map(AuctionItemResponseDTO::fromAuctionItem).collect(Collectors.toList());

			List<AuctionItemModel> itemModels = responseItems.stream().map(auctionItemModelAssembler::toModel)
					.collect(Collectors.toList());

			CollectionModel<AuctionItemModel> collectionModel = CollectionModel.of(itemModels,
					linkTo(methodOn(AuctionController.class).getAllAuctionItemsAdmin()).withSelfRel());

			return ResponseUtil.ok(collectionModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving all auction items: " + e.getMessage());
		}
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<AuctionItemResponseDTO>>> searchAuctionItems(
			@RequestParam("keyword") String keyword) {
		try {
			String sanitizedKeyword = ValidationUtil.sanitizeString(keyword);
			if (sanitizedKeyword == null || sanitizedKeyword.isEmpty()) {
				return ResponseUtil.badRequest("Search keyword cannot be empty");
			}

			List<AuctionItem> items = auctionService.searchAvailableByKeyword(sanitizedKeyword);

			List<AuctionItemResponseDTO> responseItems = items.stream().map(AuctionItemResponseDTO::fromAuctionItem)
					.collect(Collectors.toList());

			return ResponseUtil.ok(responseItems);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error searching auction items: " + e.getMessage());
		}
	}

	@GetMapping("/admin/search")
	public ResponseEntity<ApiResponse<List<AuctionItemResponseDTO>>> searchAllAuctionItems(
			@RequestParam("keyword") String keyword) {
		try {
			String sanitizedKeyword = ValidationUtil.sanitizeString(keyword);
			if (sanitizedKeyword == null || sanitizedKeyword.isEmpty()) {
				return ResponseUtil.badRequest("Search keyword cannot be empty");
			}

			List<AuctionItem> items = auctionService.searchByKeyword(sanitizedKeyword);

			List<AuctionItemResponseDTO> responseItems = items.stream().map(AuctionItemResponseDTO::fromAuctionItem)
					.collect(Collectors.toList());

			return ResponseUtil.ok(responseItems);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error searching all auction items: " + e.getMessage());
		}
	}

	@GetMapping("/get-by-id")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> getAuctionItemByID(
			@RequestParam("itemID") String itemID) {
		try {
			if (!ValidationUtil.isValidUUID(itemID)) {
				return ResponseUtil.badRequest("Invalid item ID format");
			}

			UUID itemUUID = UUID.fromString(itemID);

			AuctionItem item = auctionService.getAuctionItemWithBidsByID(itemUUID);
			if (item == null) {
				return ResponseUtil.notFound("Auction item not found");
			}

			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item, item.getBids());

			return ResponseUtil.ok(responseItem);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.badRequest("Invalid item ID format: " + e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving auction item: " + e.getMessage());
		}
	}

	@GetMapping("/get-by-name")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> getAuctionItemByName(
			@RequestParam("itemName") String itemName) {
		try {
			String sanitizedItemName = ValidationUtil.sanitizeString(itemName);
			if (sanitizedItemName == null || sanitizedItemName.isEmpty()) {
				return ResponseUtil.badRequest("Item name cannot be empty");
			}

			AuctionItem item = auctionService.getAuctionItemByName(sanitizedItemName);
			if (item == null) {
				return ResponseUtil.notFound("Auction item not found");
			}

			List<Bid> bids = bidService.getBidsByItemId(item.getItemID());

			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item, bids);

			return ResponseUtil.ok(responseItem);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving auction item: " + e.getMessage());
		}
	}

	@PostMapping("/forward/post")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> uploadForwardAuctionItem(
			@Valid @RequestBody ForwardItemDTO forwardItemDTO, HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			if (currentUser == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			if (forwardItemDTO.getItemName() == null || forwardItemDTO.getItemName().trim().isEmpty()) {
				return ResponseUtil.badRequest("Item name cannot be empty");
			}

			if (forwardItemDTO.getCurrentPrice() == null || forwardItemDTO.getCurrentPrice() <= 0) {
				return ResponseUtil.badRequest("Current price must be greater than zero");
			}

			if (forwardItemDTO.getEndTime() == null || forwardItemDTO.getEndTime().isBefore(LocalDateTime.now())) {
				return ResponseUtil.badRequest("End time must be in the future");
			}

			AuctionItem item = auctionService.createForwardItem(forwardItemDTO, currentUser);
			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item);
			return ResponseUtil.created("Forward auction item created successfully", responseItem);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error creating forward auction item: " + e.getMessage());
		}
	}

	@PostMapping("/dutch/post")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> uploadDutchAuctionItem(
			@Valid @RequestBody DutchItemDTO dutchItemDTO, HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			if (currentUser == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			if (dutchItemDTO.getItemName() == null || dutchItemDTO.getItemName().trim().isEmpty()) {
				return ResponseUtil.badRequest("Item name cannot be empty");
			}

			if (dutchItemDTO.getCurrentPrice() == null || dutchItemDTO.getCurrentPrice() <= 0) {
				return ResponseUtil.badRequest("Current price must be greater than zero");
			}

			if (dutchItemDTO.getReservePrice() == null || dutchItemDTO.getReservePrice() <= 0) {
				return ResponseUtil.badRequest("Reserve price must be greater than zero");
			}

			if (dutchItemDTO.getReservePrice() >= dutchItemDTO.getCurrentPrice()) {
				return ResponseUtil.badRequest("Reserve price must be less than current price");
			}

			AuctionItem item = auctionService.createDutchItem(dutchItemDTO, currentUser);
			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item);
			return ResponseUtil.created("Dutch auction item created successfully", responseItem);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error creating Dutch auction item: " + e.getMessage());
		}
	}

	@PostMapping("/bid")
	public ResponseEntity<ApiResponse<BidResponseDTO>> placeBid(@RequestParam("itemID") String itemID,
			@RequestParam("bidAmount") String bidAmount, HttpServletRequest request) {
		try {
			User user = getCurrentUser(request);
			if (user == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			if (!ValidationUtil.isValidUUID(itemID)) {
				return ResponseUtil.badRequest("Invalid item ID format");
			}

			Double bidAmountDouble = ValidationUtil.parseDouble(bidAmount);
			if (bidAmountDouble == null) {
				return ResponseUtil.badRequest("Bid amount must be a valid number");
			}

			if (bidAmountDouble <= 0) {
				return ResponseUtil.badRequest("Bid amount must be greater than zero");
			}

			UUID itemUUID = UUID.fromString(itemID);

			Bid bid = bidService.createBid(itemUUID, user, bidAmountDouble);
			BidResponseDTO responseDTO = BidResponseDTO.fromBid(bid);

			return ResponseUtil.ok("Bid placed successfully", responseDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.badRequest(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("An error occurred while placing bid: " + e.getMessage());
		}
	}

	@PatchMapping("/dutch/decreasePrice")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> decreaseDutchPrice(@RequestParam("itemID") String itemID,
			@RequestParam("decreaseBy") String decreaseBy, HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			if (currentUser == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			if (!ValidationUtil.isValidUUID(itemID)) {
				return ResponseUtil.badRequest("Invalid item ID format");
			}

			Double decreaseAmount = ValidationUtil.parseDouble(decreaseBy);
			if (decreaseAmount == null) {
				return ResponseUtil.badRequest("Decrease amount must be a valid number");
			}

			if (decreaseAmount <= 0) {
				return ResponseUtil.badRequest("Decrease amount must be greater than zero");
			}

			UUID itemUUID = UUID.fromString(itemID);
			AuctionItem item = auctionService.decreaseDutchPrice(itemUUID, currentUser.getUserID(), decreaseAmount);

			if (item == null) {
				return ResponseUtil.notFound("Auction item not found");
			}

			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item);
			return ResponseUtil.ok("Price decreased successfully", responseItem);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.badRequest(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("Error decreasing price: " + e.getMessage());
		}
	}

	@PostMapping("/pay")
	public ResponseEntity<ApiResponse<ReceiptResponseDTO>> processPayment(@RequestParam("itemID") String itemID,
			@Valid @RequestBody CreditCardDTO cardDetails, HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			if (currentUser == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			if (!ValidationUtil.isValidUUID(itemID)) {
				return ResponseUtil.badRequest("Invalid item ID format");
			}

			if (cardDetails.getCardNum() == null || cardDetails.getCardNum().trim().isEmpty()) {
				return ResponseUtil.badRequest("Card number cannot be empty");
			}

			if (cardDetails.getCardName() == null || cardDetails.getCardName().trim().isEmpty()) {
				return ResponseUtil.badRequest("Card name cannot be empty");
			}

			if (cardDetails.getExpDate() == null) {
				return ResponseUtil.badRequest("Expiration date cannot be empty");
			}

			if (cardDetails.getExpDate().isBefore(YearMonth.now())) {
				return ResponseUtil.badRequest("Expiration date cannot be in the past");
			}

			if (!CreditCardValidator.isValidCreditCard(cardDetails.getCardNum())) {
				return ResponseUtil.badRequest("Please enter a valid card number");
			}

			UUID itemUUID = UUID.fromString(itemID);
			Receipt receipt = paymentService.createPayment(itemUUID, currentUser, cardDetails);

			if (receipt == null) {
				return ResponseUtil.badRequest("Payment could not be processed");
			}

			ReceiptResponseDTO responseDto = ReceiptResponseDTO.fromReceipt(receipt);
			return ResponseUtil.ok("Payment processed successfully", responseDto);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.badRequest(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("Error processing payment: " + e.getMessage());
		}
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<ApiResponse<AuctionItemModel>> getAuctionItemById(@PathVariable UUID itemId) {
		try {
			AuctionItem item = auctionService.getAuctionItemById(itemId);
			if (item == null) {
				return ResponseUtil.notFound("Auction item not found with ID: " + itemId);
			}

			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item);
			AuctionItemModel itemModel = auctionItemModelAssembler.toModel(responseItem);

			return ResponseUtil.ok(itemModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving auction item: " + e.getMessage());
		}
	}

	@GetMapping("/{itemId}/bids")
	public ResponseEntity<ApiResponse<CollectionModel<BidModel>>> getBidsForItem(@PathVariable UUID itemId) {
		try {
			List<Bid> bids = bidService.getBidsByItemId(itemId);

			List<BidResponseDTO> responseBids = bids.stream().map(BidResponseDTO::fromBid).collect(Collectors.toList());

			List<BidModel> bidModels = responseBids.stream().map(bidModelAssembler::toModel)
					.collect(Collectors.toList());

			CollectionModel<BidModel> collectionModel = CollectionModel.of(bidModels,
					linkTo(methodOn(AuctionController.class).getBidsForItem(itemId)).withSelfRel(),
					linkTo(methodOn(AuctionController.class).getAuctionItemById(itemId)).withRel("auctionItem"));

			return ResponseUtil.ok(collectionModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving bids: " + e.getMessage());
		}
	}

	@PostMapping("/check-status/{itemId}")
	public ResponseEntity<ApiResponse<AuctionItemResponseDTO>> checkAuctionStatus(@PathVariable UUID itemId) {
		try {
			LocalDateTime now = LocalDateTime.now();
			AuctionItem item = auctionService.getAuctionItemById(itemId);

			if (item == null) {
				return ResponseUtil.notFound("Auction item not found");
			}

			if (item instanceof ForwardAuctionItem) {
				ForwardAuctionItem forwardItem = (ForwardAuctionItem) item;

				if (forwardItem.getAuctionStatus() == AuctionStatus.AVAILABLE
						&& forwardItem.getEndTime() != null
						&& now.isAfter(forwardItem.getEndTime())) {

					boolean hasBids = !bidService.getBidsByItemId(itemId).isEmpty();
					forwardItem.setAuctionStatus(hasBids ? AuctionStatus.SOLD : AuctionStatus.EXPIRED);
					item = auctionService.saveAuctionItem(forwardItem);
				}
			}

			AuctionItemResponseDTO responseItem = AuctionItemResponseDTO.fromAuctionItem(item);
			return ResponseUtil.ok(responseItem);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error checking auction status: " + e.getMessage());
		}
	}
}
