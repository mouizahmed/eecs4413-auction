package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.DTO.CreditCardDTO;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.ReceiptRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private ReceiptRepository receiptRepository;

	@Mock
	private AuctionWebSocketHandler auctionWebSocketHandler;

	@InjectMocks
	private PaymentService paymentService;

	private User testUser;
	private ForwardAuctionItem auctionItem;
	private CreditCardDTO creditCardDTO;
	private UUID itemId;
	private UUID userId;

	@BeforeEach
	void setUp() {

		userId = UUID.randomUUID();
		testUser = new User();
		testUser.setUserID(userId);
		testUser.setUsername("testUser");

		Address address = new Address("Main St", 123, "12345", "Test City", "Test Country");
		testUser.setAddress(address);

		itemId = UUID.randomUUID();
		auctionItem = new ForwardAuctionItem();
		auctionItem.setItemName("Test Item");
		auctionItem.setSeller(testUser);
		auctionItem.setCurrentPrice(100.0);
		auctionItem.setShippingTime(5);
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		auctionItem.setHighestBidder(testUser);

		YearMonth expDate = YearMonth.now().plusYears(1);
		creditCardDTO = new CreditCardDTO("1234567890123456", "Test User", expDate, "123");
	}

	@Test
	void testCreatePayment_Success() {

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
		when(auctionRepository.save(any(AuctionItem.class))).thenReturn(auctionItem);
		when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));
		doNothing().when(auctionWebSocketHandler).broadcastAuctionUpdate(any(AuctionItem.class));
		doNothing().when(auctionWebSocketHandler).broadcastNewPayment(any(Receipt.class));

		Receipt receipt = paymentService.createPayment(itemId, testUser, creditCardDTO);

		assertNotNull(receipt);
		assertEquals(itemId, receipt.getItemID());
		assertEquals(testUser, receipt.getUser());
		assertEquals(auctionItem.getCurrentPrice(), receipt.getTotalCost());
		assertEquals(testUser.getAddress(), receipt.getAddress());
		assertEquals(auctionItem.getShippingTime(), receipt.getShippingTime());

		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, times(1)).save(auctionItem);
		verify(receiptRepository, times(1)).save(any(Receipt.class));
		verify(auctionWebSocketHandler, times(1)).broadcastAuctionUpdate(auctionItem);
		verify(auctionWebSocketHandler, times(1)).broadcastNewPayment(any(Receipt.class));
	}

	@Test
	void testCreatePayment_ItemNotFound() {

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.createPayment(itemId, testUser, creditCardDTO);
		});

		assertEquals("Auction item not found", exception.getMessage());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, never()).save(any(AuctionItem.class));
		verify(receiptRepository, never()).save(any(Receipt.class));
	}

	@Test
	void testCreatePayment_NotWinningBidder() {

		User otherUser = new User();
		otherUser.setUserID(UUID.randomUUID());
		otherUser.setUsername("otherUser");

		auctionItem.setHighestBidder(otherUser);

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.createPayment(itemId, testUser, creditCardDTO);
		});

		assertEquals("You must be the winning bidder to place a payment on this item.", exception.getMessage());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, never()).save(any(AuctionItem.class));
		verify(receiptRepository, never()).save(any(Receipt.class));
	}

	@Test
	void testCreatePayment_WrongStatus() {

		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE); // Wrong status

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.createPayment(itemId, testUser, creditCardDTO);
		});

		assertEquals("The auction is either over or still ongoing.", exception.getMessage());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, never()).save(any(AuctionItem.class));
		verify(receiptRepository, never()).save(any(Receipt.class));
	}
}