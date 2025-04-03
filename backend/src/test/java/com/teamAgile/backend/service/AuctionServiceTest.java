package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.AuctionItem.AuctionType;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionWebSocketHandler auctionWebSocketHandler;

    @InjectMocks
    private AuctionService auctionService;

    private User testUser;
    private DutchItemDTO testDutchItemDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserID(UUID.randomUUID());
        testUser.setUsername("testUser");

        testDutchItemDTO = new DutchItemDTO();
        testDutchItemDTO.setItemName("Test Item");
        testDutchItemDTO.setCurrentPrice(100.0);
        testDutchItemDTO.setReservePrice(50.0);
        testDutchItemDTO.setShippingTime(5);
        testDutchItemDTO.setAuctionType(AuctionType.DUTCH);
        testDutchItemDTO.setAuctionStatus(AuctionStatus.AVAILABLE);
    }

    @Test
    void createDutchItem_Success() {
        when(auctionRepository.findByItemName(any())).thenReturn(Optional.empty());
        when(auctionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuctionItem result = auctionService.createDutchItem(testDutchItemDTO, testUser);

        assertNotNull(result);
        assertTrue(result instanceof DutchAuctionItem);
        assertEquals(testDutchItemDTO.getItemName(), result.getItemName());
        assertEquals(testDutchItemDTO.getCurrentPrice(), result.getCurrentPrice());
        assertEquals(testDutchItemDTO.getShippingTime(), result.getShippingTime());
        assertEquals(testUser, result.getSeller());
        assertEquals(AuctionStatus.AVAILABLE, result.getAuctionStatus());
        assertEquals(AuctionType.DUTCH, result.getAuctionType());
        assertEquals(testDutchItemDTO.getReservePrice(), ((DutchAuctionItem) result).getReservePrice());
    }

    @Test
    void createDutchItem_DuplicateItemName() {
        when(auctionRepository.findByItemName(any())).thenReturn(Optional.of(new DutchAuctionItem()));

        assertThrows(IllegalArgumentException.class, () -> auctionService.createDutchItem(testDutchItemDTO, testUser));
    }

    @Test
    void createDutchItem_InvalidAuctionType() {
        testDutchItemDTO.setAuctionType(AuctionType.FORWARD);

        assertThrows(IllegalArgumentException.class, () -> auctionService.createDutchItem(testDutchItemDTO, testUser));
    }

    @Test
    void createDutchItem_NullReservePrice() {
        testDutchItemDTO.setReservePrice(null);

        assertThrows(IllegalArgumentException.class, () -> auctionService.createDutchItem(testDutchItemDTO, testUser));
    }

    @Test
    void decreaseDutchPrice_Success() {
        DutchAuctionItem dutchItem = new DutchAuctionItem(
                "Test Item", testUser, AuctionStatus.AVAILABLE, 100.0, 5, 50.0);
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.of(dutchItem));
        when(auctionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuctionItem result = auctionService.decreaseDutchPrice(dutchItem.getItemID(), testUser.getUserID(), 20.0);

        assertNotNull(result);
        assertTrue(result instanceof DutchAuctionItem);
        assertEquals(80.0, result.getCurrentPrice());
        assertEquals(AuctionStatus.AVAILABLE, result.getAuctionStatus());
        verify(auctionWebSocketHandler).broadcastAuctionUpdate(any());
    }

    @Test
    void decreaseDutchPrice_ItemNotFound() {
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> auctionService.decreaseDutchPrice(UUID.randomUUID(), testUser.getUserID(), 20.0));
    }

    @Test
    void decreaseDutchPrice_NotSeller() {
        User differentUser = new User();
        differentUser.setUserID(UUID.randomUUID());
        DutchAuctionItem dutchItem = new DutchAuctionItem(
                "Test Item", differentUser, AuctionStatus.AVAILABLE, 100.0, 5, 50.0);
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.of(dutchItem));

        assertThrows(IllegalArgumentException.class,
                () -> auctionService.decreaseDutchPrice(dutchItem.getItemID(), testUser.getUserID(), 20.0));
    }

    @Test
    void decreaseDutchPrice_ItemNotAvailable() {
        DutchAuctionItem dutchItem = new DutchAuctionItem(
                "Test Item", testUser, AuctionStatus.SOLD, 100.0, 5, 50.0);
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.of(dutchItem));

        assertThrows(IllegalArgumentException.class,
                () -> auctionService.decreaseDutchPrice(dutchItem.getItemID(), testUser.getUserID(), 20.0));
    }

    @Test
    void decreaseDutchPrice_NotDutchAuction() {
        AuctionItem nonDutchItem = new AuctionItem() {
            @Override
            public void placeBid(Double bidAmount, User user) {
            }
        };
        nonDutchItem.setSeller(testUser);
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.of(nonDutchItem));

        assertThrows(IllegalArgumentException.class,
                () -> auctionService.decreaseDutchPrice(nonDutchItem.getItemID(), testUser.getUserID(), 20.0));
    }

    @Test
    void decreaseDutchPrice_ReachesReservePrice() {
        DutchAuctionItem dutchItem = new DutchAuctionItem(
                "Test Item", testUser, AuctionStatus.AVAILABLE, 100.0, 5, 50.0);
        when(auctionRepository.findByItemID(any())).thenReturn(Optional.of(dutchItem));
        when(auctionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuctionItem result = auctionService.decreaseDutchPrice(dutchItem.getItemID(), testUser.getUserID(), 60.0);

        assertNotNull(result);
        assertTrue(result instanceof DutchAuctionItem);
        assertEquals(50.0, result.getCurrentPrice());
        assertEquals(AuctionStatus.EXPIRED, result.getAuctionStatus());
        verify(auctionWebSocketHandler).broadcastAuctionUpdate(any());
    }
}