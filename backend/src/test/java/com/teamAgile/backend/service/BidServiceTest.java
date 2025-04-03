package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionWebSocketHandler auctionWebSocketHandler;

    @InjectMocks
    private BidService bidService;

    private User bidder;
    private User seller;
    private AuctionItem auctionItem;
    private Bid testBid;
    private UUID itemId;
    private UUID bidderId;
    private UUID bidId;

    @BeforeEach
    void setUp() {
        // Set up test users
        bidder = new User();
        bidderId = UUID.randomUUID();
        bidder.setUserID(bidderId);
        bidder.setUsername("bidder");

        seller = new User();
        seller.setUserID(UUID.randomUUID());
        seller.setUsername("seller");

        // Set up test auction item with mock
        itemId = UUID.randomUUID();
        auctionItem = mock(ForwardAuctionItem.class);

        // Use lenient() to avoid "unnecessary stubbing" errors
        lenient().when(auctionItem.getItemID()).thenReturn(itemId);
        lenient().when(auctionItem.getItemName()).thenReturn("Test Item");
        lenient().when(auctionItem.getCurrentPrice()).thenReturn(100.0);
        lenient().when(auctionItem.getSeller()).thenReturn(seller);
        lenient().when(auctionItem.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);

        // Set up test bid
        testBid = new Bid(itemId, bidder, 120.0);
        bidId = UUID.randomUUID();
        // Using reflection to set bidID since it's normally set by the database
        try {
            java.lang.reflect.Field field = testBid.getClass().getDeclaredField("bidID");
            field.setAccessible(true);
            field.set(testBid, bidId);
        } catch (Exception e) {
            // Handle reflection exception
        }
    }

    @Test
    void createBid_Success() {
        // Arrange
        Double bidAmount = 120.0;
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
        when(auctionRepository.save(any(AuctionItem.class))).thenReturn(auctionItem);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Bid result = bidService.createBid(itemId, bidder, bidAmount);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemID());
        assertEquals(bidder, result.getUser());
        assertEquals(bidAmount, result.getBidAmount());

        // Verify interactions
        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionRepository).save(auctionItem);
        verify(bidRepository).save(any(Bid.class));
        verify(auctionWebSocketHandler).broadcastAuctionUpdate(auctionItem);
        verify(auctionWebSocketHandler).broadcastNewBid(any(Bid.class));
    }

    @Test
    void createBid_UnpaidItems() {
        // Arrange
        List<AuctionItem> unpaidItems = new ArrayList<>();
        unpaidItems.add(auctionItem);
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(unpaidItems);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("You have unpaid items. Please pay for your won auctions before placing new bids.",
                exception.getMessage());

        // Verify interactions
        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository, never()).findByItemID(any());
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void createBid_ItemNotFound() {
        // Arrange
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("Auction item not found", exception.getMessage());

        // Verify interactions
        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void createBid_ItemPlaceBidError() {
        // Arrange
        AuctionItem mockItem = mock(AuctionItem.class);
        doThrow(new IllegalArgumentException("Bid amount too low"))
                .when(mockItem).placeBid(anyDouble(), any(User.class));

        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(mockItem));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("Bid amount too low", exception.getMessage());

        // Verify interactions
        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(mockItem).placeBid(120.0, bidder);
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void getBidsByItemId_Success() {
        // Arrange
        List<Bid> expectedBids = new ArrayList<>();
        expectedBids.add(new Bid(itemId, bidder, 120.0));
        expectedBids.add(new Bid(itemId, bidder, 110.0));

        when(bidRepository.findByItemIDOrderByBidAmountDesc(itemId)).thenReturn(expectedBids);

        // Act
        List<Bid> result = bidService.getBidsByItemId(itemId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBids, result);

        // Verify interactions
        verify(bidRepository).findByItemIDOrderByBidAmountDesc(itemId);
    }

    @Test
    void getBidsByItemId_EmptyResult() {
        // Arrange
        when(bidRepository.findByItemIDOrderByBidAmountDesc(itemId)).thenReturn(new ArrayList<>());

        // Act
        List<Bid> result = bidService.getBidsByItemId(itemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(bidRepository).findByItemIDOrderByBidAmountDesc(itemId);
    }

    @Test
    void getBidsByUserId_Success() {
        // Arrange
        List<Bid> expectedBids = new ArrayList<>();
        expectedBids.add(new Bid(itemId, bidder, 120.0));
        expectedBids.add(new Bid(UUID.randomUUID(), bidder, 90.0));

        when(bidRepository.findByUser_UserID(bidderId)).thenReturn(expectedBids);

        // Act
        List<Bid> result = bidService.getBidsByUserId(bidderId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBids, result);

        // Verify interactions
        verify(bidRepository).findByUser_UserID(bidderId);
    }

    @Test
    void getBidsByUserId_EmptyResult() {
        // Arrange
        when(bidRepository.findByUser_UserID(bidderId)).thenReturn(new ArrayList<>());

        // Act
        List<Bid> result = bidService.getBidsByUserId(bidderId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(bidRepository).findByUser_UserID(bidderId);
    }

    @Test
    void getBidById_Found() {
        // Arrange
        when(bidRepository.findByBidID(bidId)).thenReturn(Optional.of(testBid));

        // Act
        Optional<Bid> result = bidService.getBidById(bidId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBid, result.get());

        // Verify interactions
        verify(bidRepository).findByBidID(bidId);
    }

    @Test
    void getBidById_NotFound() {
        // Arrange
        when(bidRepository.findByBidID(bidId)).thenReturn(Optional.empty());

        // Act
        Optional<Bid> result = bidService.getBidById(bidId);

        // Assert
        assertFalse(result.isPresent());

        // Verify interactions
        verify(bidRepository).findByBidID(bidId);
    }
}