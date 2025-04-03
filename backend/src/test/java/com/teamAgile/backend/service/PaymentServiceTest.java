package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
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
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.ReceiptRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private AuctionWebSocketHandler auctionWebSocketHandler;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private AuctionItem auctionItem;
    private CreditCardDTO creditCardDTO;
    private Receipt testReceipt;
    private UUID itemId;
    private UUID userId;
    private UUID receiptId;

    @BeforeEach
    void setUp() {
        // Set up test user
        user = new User();
        userId = UUID.randomUUID();
        user.setUserID(userId);
        user.setUsername("testUser");

        // Set up test address
        Address address = new Address();
        address.setStreetName("Test Street");
        address.setStreetNum(123);
        address.setPostalCode("A1A1A1");
        address.setCity("Test City");
        address.setProvince("Test Province");
        address.setCountry("Test Country");
        user.setAddress(address);

        // Set up test auction item
        itemId = UUID.randomUUID();
        auctionItem = mock(ForwardAuctionItem.class);

        // Use lenient() to avoid "unnecessary stubbing" errors
        lenient().when(auctionItem.getItemID()).thenReturn(itemId);
        lenient().when(auctionItem.getItemName()).thenReturn("Test Item");
        lenient().when(auctionItem.getCurrentPrice()).thenReturn(100.0);
        lenient().when(auctionItem.getShippingTime()).thenReturn(5);
        lenient().when(auctionItem.getAuctionStatus()).thenReturn(AuctionStatus.SOLD);
        lenient().when(auctionItem.getHighestBidder()).thenReturn(user);

        // Set up test credit card DTO
        creditCardDTO = new CreditCardDTO();
        creditCardDTO.setCardNumber("4111111111111111");
        creditCardDTO.setCardName("Test User");
        creditCardDTO.setExpDate(YearMonth.of(2025, 12));
        creditCardDTO.setSecurityCode("123");

        // Set up test receipt
        receiptId = UUID.randomUUID();
        testReceipt = mock(Receipt.class);

        // Use lenient() to avoid "unnecessary stubbing" errors
        lenient().when(testReceipt.getReceiptID()).thenReturn(receiptId);
        lenient().when(testReceipt.getUser()).thenReturn(user);
        lenient().when(testReceipt.getItemID()).thenReturn(itemId);
        lenient().when(testReceipt.getTotalCost()).thenReturn(100.0);
    }

    @Test
    void createPayment_Success() {
        // Arrange
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
        doNothing().when(auctionItem).makePayment(user);
        when(auctionRepository.save(any(AuctionItem.class))).thenReturn(auctionItem);
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(i -> {
            Receipt receipt = i.getArgument(0);
            // Using reflection to set receiptID since it's normally set by the database
            try {
                java.lang.reflect.Field field = receipt.getClass().getDeclaredField("receiptID");
                field.setAccessible(true);
                field.set(receipt, receiptId);
            } catch (Exception e) {
                // Handle reflection exception
            }
            return receipt;
        });

        // Act
        Receipt result = paymentService.createPayment(itemId, user, creditCardDTO);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemID());
        assertEquals(user, result.getUser());
        assertEquals(100.0, result.getTotalCost(), 0.001);

        // Verify interactions
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionItem).makePayment(user);
        verify(auctionRepository).save(auctionItem);
        verify(receiptRepository).save(any(Receipt.class));
        verify(auctionWebSocketHandler).broadcastAuctionUpdate(auctionItem);
        verify(auctionWebSocketHandler).broadcastNewPayment(any(Receipt.class));
    }

    @Test
    void createPayment_ItemNotFound() {
        // Arrange
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.createPayment(itemId, user, creditCardDTO));
        assertEquals("Auction item not found", exception.getMessage());

        // Verify interactions
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionRepository, never()).save(any());
        verify(receiptRepository, never()).save(any());
    }

    @Test
    void createPayment_MakePaymentError() {
        // Arrange
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
        doThrow(new IllegalArgumentException("User is not the winning bidder"))
                .when(auctionItem).makePayment(user);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.createPayment(itemId, user, creditCardDTO));
        assertEquals("User is not the winning bidder", exception.getMessage());

        // Verify interactions
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionItem).makePayment(user);
        verify(auctionRepository, never()).save(any());
        verify(receiptRepository, never()).save(any());
    }

    @Test
    void getReceiptById_Success() {
        // Arrange
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(testReceipt));

        // Act
        Receipt result = paymentService.getReceiptById(receiptId);

        // Assert
        assertNotNull(result);
        assertEquals(testReceipt, result);

        // Verify interactions
        verify(receiptRepository).findById(receiptId);
    }

    @Test
    void getReceiptById_NotFound() {
        // Arrange
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.getReceiptById(receiptId));
        assertEquals("Receipt not found", exception.getMessage());

        // Verify interactions
        verify(receiptRepository).findById(receiptId);
    }

    @Test
    void getReceiptsByUserId_Success() {
        // Arrange
        List<Receipt> expectedReceipts = new ArrayList<>();
        expectedReceipts.add(testReceipt);

        when(receiptRepository.findByUser_UserID(userId)).thenReturn(expectedReceipts);

        // Act
        List<Receipt> result = paymentService.getReceiptsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedReceipts, result);

        // Verify interactions
        verify(receiptRepository).findByUser_UserID(userId);
    }

    @Test
    void getReceiptsByUserId_EmptyResult() {
        // Arrange
        when(receiptRepository.findByUser_UserID(userId)).thenReturn(new ArrayList<>());

        // Act
        List<Receipt> result = paymentService.getReceiptsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(receiptRepository).findByUser_UserID(userId);
    }
}