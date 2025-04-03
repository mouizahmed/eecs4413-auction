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
        bidder = new User();
        bidderId = UUID.randomUUID();
        bidder.setUserID(bidderId);
        bidder.setUsername("bidder");

        seller = new User();
        seller.setUserID(UUID.randomUUID());
        seller.setUsername("seller");

        itemId = UUID.randomUUID();
        auctionItem = mock(ForwardAuctionItem.class);

        lenient().when(auctionItem.getItemID()).thenReturn(itemId);
        lenient().when(auctionItem.getItemName()).thenReturn("Test Item");
        lenient().when(auctionItem.getCurrentPrice()).thenReturn(100.0);
        lenient().when(auctionItem.getSeller()).thenReturn(seller);
        lenient().when(auctionItem.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);

        testBid = new Bid(itemId, bidder, 120.0);
        bidId = UUID.randomUUID();
        try {
            java.lang.reflect.Field field = testBid.getClass().getDeclaredField("bidID");
            field.setAccessible(true);
            field.set(testBid, bidId);
        } catch (Exception e) {
        }
    }

    @Test
    void createBid_Success() {
        Double bidAmount = 120.0;
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
        when(auctionRepository.save(any(AuctionItem.class))).thenReturn(auctionItem);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));

        Bid result = bidService.createBid(itemId, bidder, bidAmount);

        assertNotNull(result);
        assertEquals(itemId, result.getItemID());
        assertEquals(bidder, result.getUser());
        assertEquals(bidAmount, result.getBidAmount());

        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionRepository).save(auctionItem);
        verify(bidRepository).save(any(Bid.class));
        verify(auctionWebSocketHandler).broadcastAuctionUpdate(auctionItem);
        verify(auctionWebSocketHandler).broadcastNewBid(any(Bid.class));
    }

    @Test
    void createBid_UnpaidItems() {
        List<AuctionItem> unpaidItems = new ArrayList<>();
        unpaidItems.add(auctionItem);
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(unpaidItems);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("You have unpaid items. Please pay for your won auctions before placing new bids.",
                exception.getMessage());

        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository, never()).findByItemID(any());
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void createBid_ItemNotFound() {
        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("Auction item not found", exception.getMessage());

        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void createBid_ItemPlaceBidError() {
        AuctionItem mockItem = mock(AuctionItem.class);
        doThrow(new IllegalArgumentException("Bid amount too low"))
                .when(mockItem).placeBid(anyDouble(), any(User.class));

        when(auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD))
                .thenReturn(new ArrayList<>());
        when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(mockItem));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bidService.createBid(itemId, bidder, 120.0));
        assertEquals("Bid amount too low", exception.getMessage());

        verify(auctionRepository).findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);
        verify(auctionRepository).findByItemID(itemId);
        verify(mockItem).placeBid(120.0, bidder);
        verify(auctionRepository, never()).save(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void getBidsByItemId_Success() {
        List<Bid> expectedBids = new ArrayList<>();
        expectedBids.add(new Bid(itemId, bidder, 120.0));
        expectedBids.add(new Bid(itemId, bidder, 110.0));

        when(bidRepository.findByItemIDOrderByBidAmountDesc(itemId)).thenReturn(expectedBids);

        List<Bid> result = bidService.getBidsByItemId(itemId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBids, result);

        verify(bidRepository).findByItemIDOrderByBidAmountDesc(itemId);
    }

    @Test
    void getBidsByItemId_EmptyResult() {
        when(bidRepository.findByItemIDOrderByBidAmountDesc(itemId)).thenReturn(new ArrayList<>());

        List<Bid> result = bidService.getBidsByItemId(itemId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bidRepository).findByItemIDOrderByBidAmountDesc(itemId);
    }

    @Test
    void getBidsByUserId_Success() {
        List<Bid> expectedBids = new ArrayList<>();
        expectedBids.add(new Bid(itemId, bidder, 120.0));
        expectedBids.add(new Bid(UUID.randomUUID(), bidder, 90.0));

        when(bidRepository.findByUser_UserID(bidderId)).thenReturn(expectedBids);

        List<Bid> result = bidService.getBidsByUserId(bidderId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBids, result);

        verify(bidRepository).findByUser_UserID(bidderId);
    }

    @Test
    void getBidsByUserId_EmptyResult() {
        when(bidRepository.findByUser_UserID(bidderId)).thenReturn(new ArrayList<>());

        List<Bid> result = bidService.getBidsByUserId(bidderId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bidRepository).findByUser_UserID(bidderId);
    }

    @Test
    void getBidById_Found() {
        when(bidRepository.findByBidID(bidId)).thenReturn(Optional.of(testBid));

        Optional<Bid> result = bidService.getBidById(bidId);

        assertTrue(result.isPresent());
        assertEquals(testBid, result.get());

        verify(bidRepository).findByBidID(bidId);
    }

    @Test
    void getBidById_NotFound() {
        when(bidRepository.findByBidID(bidId)).thenReturn(Optional.empty());

        Optional<Bid> result = bidService.getBidById(bidId);

        assertFalse(result.isPresent());

        verify(bidRepository).findByBidID(bidId);
    }
}