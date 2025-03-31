package com.teamAgile.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.repository.BidRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerServiceTest {

	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private BidRepository bidRepository;

	@Mock
	private AuctionWebSocketHandler auctionWebSocketHandler;

	@InjectMocks
	private AuctionSchedulerService auctionSchedulerService;

	@Test
	void testCheckAndUpdateExpiredAuctions() {
		// Create test data
		ForwardAuctionItem activeAuctionItem = mock(ForwardAuctionItem.class);
		when(activeAuctionItem.getEndTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(activeAuctionItem.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);

		ForwardAuctionItem expiredAuctionItemWithBids = mock(ForwardAuctionItem.class);
		UUID expiredWithBidsId = UUID.randomUUID();
		when(expiredAuctionItemWithBids.getEndTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(expiredAuctionItemWithBids.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);
		when(expiredAuctionItemWithBids.getItemID()).thenReturn(expiredWithBidsId);

		ForwardAuctionItem expiredAuctionItemWithoutBids = mock(ForwardAuctionItem.class);
		UUID expiredWithoutBidsId = UUID.randomUUID();
		when(expiredAuctionItemWithoutBids.getEndTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(expiredAuctionItemWithoutBids.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);
		when(expiredAuctionItemWithoutBids.getItemID()).thenReturn(expiredWithoutBidsId);

		List<AuctionItem> auctionItems = Arrays.asList(activeAuctionItem, expiredAuctionItemWithBids,
				expiredAuctionItemWithoutBids);
		List<Bid> bids = Collections.singletonList(mock(Bid.class));

		// Set up mocks
		when(auctionRepository.findAll()).thenReturn(auctionItems);
		when(bidRepository.findByItemIDOrderByBidAmountDesc(expiredWithBidsId)).thenReturn(bids);
		when(bidRepository.findByItemIDOrderByBidAmountDesc(expiredWithoutBidsId)).thenReturn(Collections.emptyList());

		// Execute
		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		// Verify
		verify(expiredAuctionItemWithBids).setAuctionStatus(AuctionStatus.SOLD);
		verify(auctionRepository).save(expiredAuctionItemWithBids);
		verify(auctionWebSocketHandler).broadcastAuctionUpdate(any(AuctionItem.class));

		verify(expiredAuctionItemWithoutBids).setAuctionStatus(AuctionStatus.EXPIRED);
		verify(auctionRepository).save(expiredAuctionItemWithoutBids);
		verify(auctionWebSocketHandler).broadcastAuctionUpdate(any(AuctionItem.class));

		verify(activeAuctionItem, never()).setAuctionStatus(any());
		verify(activeAuctionItem, never()).setHighestBidder(any());
	}

	@Test
	void testCheckAndUpdateExpiredAuctions_NoExpiredAuctions() {
		// Create test data
		ForwardAuctionItem activeAuctionItem = mock(ForwardAuctionItem.class);
		when(activeAuctionItem.getEndTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(activeAuctionItem.getAuctionStatus()).thenReturn(AuctionStatus.AVAILABLE);

		List<AuctionItem> activeAuctions = Collections.singletonList(activeAuctionItem);

		// Set up mocks
		when(auctionRepository.findAll()).thenReturn(activeAuctions);

		// Execute
		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		// Verify
		verify(activeAuctionItem, never()).setAuctionStatus(any());
		verify(activeAuctionItem, never()).setHighestBidder(any());
		verify(auctionRepository, never()).save(any());
		verify(bidRepository, never()).findByItemIDOrderByBidAmountDesc(any());
	}

	@Test
	void testCheckAndUpdateExpiredAuctions_NonForwardAuctionItems() {
		// Create test data
		AuctionItem dutchAuctionItem = mock(AuctionItem.class);
		List<AuctionItem> mixedAuctions = Collections.singletonList(dutchAuctionItem);

		// Set up mocks
		when(auctionRepository.findAll()).thenReturn(mixedAuctions);

		// Execute
		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		// Verify
		verify(dutchAuctionItem, never()).setAuctionStatus(any());
		verify(auctionRepository, never()).save(any());
		verify(bidRepository, never()).findByItemIDOrderByBidAmountDesc(any());
	}
}