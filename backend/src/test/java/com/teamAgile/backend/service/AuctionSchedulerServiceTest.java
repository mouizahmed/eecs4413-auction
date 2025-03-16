package com.teamAgile.backend.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

		ForwardAuctionItem activeAuctionItem = mock(ForwardAuctionItem.class);
		when(activeAuctionItem.getEndTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(activeAuctionItem.getAuctionStatus()).thenReturn(AuctionItem.AuctionStatus.AVAILABLE);

		ForwardAuctionItem expiredAuctionItemWithBids = mock(ForwardAuctionItem.class);
		UUID expiredWithBidsId = UUID.randomUUID();
		when(expiredAuctionItemWithBids.getEndTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(expiredAuctionItemWithBids.getAuctionStatus()).thenReturn(AuctionItem.AuctionStatus.AVAILABLE);
		when(expiredAuctionItemWithBids.getItemID()).thenReturn(expiredWithBidsId);

		ForwardAuctionItem expiredAuctionItemWithoutBids = mock(ForwardAuctionItem.class);
		UUID expiredWithoutBidsId = UUID.randomUUID();
		when(expiredAuctionItemWithoutBids.getEndTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(expiredAuctionItemWithoutBids.getAuctionStatus()).thenReturn(AuctionItem.AuctionStatus.AVAILABLE);
		when(expiredAuctionItemWithoutBids.getItemID()).thenReturn(expiredWithoutBidsId);

		List<AuctionItem> auctionItems = new ArrayList<>();
		auctionItems.add(activeAuctionItem);
		auctionItems.add(expiredAuctionItemWithBids);
		auctionItems.add(expiredAuctionItemWithoutBids);

		List<Bid> bids = new ArrayList<>();
		bids.add(mock(Bid.class));

		when(auctionRepository.findAll()).thenReturn(auctionItems);
		when(bidRepository.findByItemID(expiredWithBidsId)).thenReturn(bids);
		when(bidRepository.findByItemID(expiredWithoutBidsId)).thenReturn(new ArrayList<>());

		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		verify(expiredAuctionItemWithBids).setAuctionStatus(AuctionItem.AuctionStatus.SOLD);
		verify(auctionRepository).save(expiredAuctionItemWithBids);

		verify(expiredAuctionItemWithoutBids).setAuctionStatus(AuctionItem.AuctionStatus.EXPIRED);
		verify(auctionRepository).save(expiredAuctionItemWithoutBids);

		verify(activeAuctionItem, never()).setAuctionStatus(any());
		verify(activeAuctionItem, never()).setHighestBidder(any());
	}

	@Test
	void testCheckAndUpdateExpiredAuctions_NoExpiredAuctions() {

		ForwardAuctionItem activeAuctionItem = mock(ForwardAuctionItem.class);
		when(activeAuctionItem.getEndTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(activeAuctionItem.getAuctionStatus()).thenReturn(AuctionItem.AuctionStatus.AVAILABLE);

		List<AuctionItem> activeAuctions = new ArrayList<>();
		activeAuctions.add(activeAuctionItem);

		when(auctionRepository.findAll()).thenReturn(activeAuctions);

		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		verify(activeAuctionItem, never()).setAuctionStatus(any());
		verify(activeAuctionItem, never()).setHighestBidder(any());
		verify(auctionRepository, never()).save(any());
	}

	@Test
	void testCheckAndUpdateExpiredAuctions_NonForwardAuctionItems() {

		AuctionItem dutchAuctionItem = mock(AuctionItem.class);

		List<AuctionItem> mixedAuctions = new ArrayList<>();
		mixedAuctions.add(dutchAuctionItem);

		when(auctionRepository.findAll()).thenReturn(mixedAuctions);

		auctionSchedulerService.checkAndUpdateExpiredAuctions();

		verify(dutchAuctionItem, never()).setAuctionStatus(any());
		verify(auctionRepository, never()).save(dutchAuctionItem);
	}
}