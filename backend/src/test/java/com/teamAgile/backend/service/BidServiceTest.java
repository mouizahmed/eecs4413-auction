package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

	@Mock
	private BidRepository bidRepository;

	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private AuctionWebSocketHandler auctionWebSocketHandler;

	@InjectMocks
	private BidService bidService;

	private User testUser;
	private ForwardAuctionItem auctionItem;
	private Bid bid;
	private UUID itemId;
	private UUID userId;
	private UUID bidId;
	private Double bidAmount;

	@BeforeEach
	void setUp() throws Exception {

		userId = UUID.randomUUID();
		testUser = new User();
		testUser.setUserID(userId);
		testUser.setUsername("testUser");

		itemId = UUID.randomUUID();
		auctionItem = new ForwardAuctionItem();
		auctionItem.setItemName("Test Item");
		auctionItem.setSeller(testUser);
		auctionItem.setCurrentPrice(100.0);
		auctionItem.setShippingTime(5);
		auctionItem.setEndTime(LocalDateTime.now().plusDays(7));
		auctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		Field field = AuctionItem.class.getDeclaredField("auctionType");
		field.setAccessible(true);
		field.set(auctionItem, AuctionItem.AuctionType.FORWARD);

		bidId = UUID.randomUUID();
		bidAmount = 150.0;
		bid = new Bid(itemId, testUser, bidAmount);
	}

	@Test
	void testCreateBid_Success() {

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(auctionItem));
		when(auctionRepository.save(any(AuctionItem.class))).thenReturn(auctionItem);
		when(bidRepository.save(any(Bid.class))).thenReturn(bid);
		doNothing().when(auctionWebSocketHandler).broadcastAuctionUpdate(any(AuctionItem.class));
		doNothing().when(auctionWebSocketHandler).broadcastNewBid(any(Bid.class));

		Bid createdBid = bidService.createBid(itemId, testUser, bidAmount);

		assertNotNull(createdBid);
		assertEquals(itemId, createdBid.getItemID());
		assertEquals(testUser, createdBid.getUser());
		assertEquals(bidAmount, createdBid.getBidAmount());

		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, times(1)).save(any(AuctionItem.class));
		verify(bidRepository, times(1)).save(any(Bid.class));
		verify(auctionWebSocketHandler, times(1)).broadcastAuctionUpdate(any(AuctionItem.class));
		verify(auctionWebSocketHandler, times(1)).broadcastNewBid(any(Bid.class));
	}

	@Test
	void testCreateBid_ItemNotFound() {

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			bidService.createBid(itemId, testUser, bidAmount);
		});

		assertEquals("Auction item not found", exception.getMessage());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, never()).save(any(AuctionItem.class));
		verify(bidRepository, never()).save(any(Bid.class));
	}

	@Test
	void testCreateBid_InvalidBid() {

		ForwardAuctionItem mockAuctionItem = mock(ForwardAuctionItem.class);
		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(mockAuctionItem));

		doThrow(new IllegalArgumentException("Bid amount must be higher than current price")).when(mockAuctionItem)
				.placeBid(eq(50.0), eq(testUser));

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			bidService.createBid(itemId, testUser, 50.0); // Lower bid amount
		});

		assertEquals("Bid amount must be higher than current price", exception.getMessage());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, never()).save(any(AuctionItem.class));
		verify(bidRepository, never()).save(any(Bid.class));
	}

	@Test
	void testGetBidsByItemId() {

		List<Bid> expectedBids = new ArrayList<>();
		expectedBids.add(bid);

		when(bidRepository.findByItemID(itemId)).thenReturn(expectedBids);

		List<Bid> actualBids = bidService.getBidsByItemId(itemId);

		assertEquals(expectedBids.size(), actualBids.size());
		assertEquals(expectedBids, actualBids);
		verify(bidRepository, times(1)).findByItemID(itemId);
	}

	@Test
	void testGetBidsByUserId() {

		List<Bid> expectedBids = new ArrayList<>();
		expectedBids.add(bid);

		when(bidRepository.findByUser_UserID(userId)).thenReturn(expectedBids);

		List<Bid> actualBids = bidService.getBidsByUserId(userId);

		assertEquals(expectedBids.size(), actualBids.size());
		assertEquals(expectedBids, actualBids);
		verify(bidRepository, times(1)).findByUser_UserID(userId);
	}

	@Test
	void testGetBidById_Found() {

		when(bidRepository.findByBidID(bidId)).thenReturn(Optional.of(bid));

		Optional<Bid> actualBid = bidService.getBidById(bidId);

		assertTrue(actualBid.isPresent());
		assertEquals(bid, actualBid.get());
		verify(bidRepository, times(1)).findByBidID(bidId);
	}

	@Test
	void testGetBidById_NotFound() {

		when(bidRepository.findByBidID(bidId)).thenReturn(Optional.empty());

		Optional<Bid> actualBid = bidService.getBidById(bidId);

		assertFalse(actualBid.isPresent());
		verify(bidRepository, times(1)).findByBidID(bidId);
	}
}