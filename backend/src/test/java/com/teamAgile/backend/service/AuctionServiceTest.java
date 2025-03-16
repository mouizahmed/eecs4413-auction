package com.teamAgile.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.DutchAuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.AuctionRepository;
import com.teamAgile.backend.websocket.AuctionWebSocketHandler;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private AuctionWebSocketHandler auctionWebSocketHandler;

	@Mock
	private BidService bidService;

	@InjectMocks
	private AuctionService auctionService;

	private User testUser;
	private ForwardAuctionItem forwardItem;
	private DutchAuctionItem dutchItem;
	private UUID itemId;
	private UUID userId;

	@BeforeEach
	void setUp() {

		userId = UUID.randomUUID();
		testUser = new User();
		testUser.setUserID(userId);
		testUser.setUsername("testUser");

		itemId = UUID.randomUUID();
		forwardItem = new ForwardAuctionItem();
		forwardItem.setItemName("Test Forward Item");
		forwardItem.setSeller(testUser);
		forwardItem.setCurrentPrice(100.0);
		forwardItem.setShippingTime(5);
		forwardItem.setEndTime(LocalDateTime.now().plusDays(7));
		forwardItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		dutchItem = new DutchAuctionItem();
		dutchItem.setItemName("Test Dutch Item");
		dutchItem.setSeller(testUser);
		dutchItem.setCurrentPrice(200.0);
		dutchItem.setShippingTime(5);
		dutchItem.setReservePrice(100.0);
		dutchItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
	}

	@Test
	void testGetAllAuctionItems() {

		List<AuctionItem> expectedItems = new ArrayList<>();
		expectedItems.add(forwardItem);
		expectedItems.add(dutchItem);

		when(auctionRepository.findAll()).thenReturn(expectedItems);

		List<AuctionItem> actualItems = auctionService.getAllAuctionItems();

		assertEquals(expectedItems.size(), actualItems.size());
		assertEquals(expectedItems, actualItems);
		verify(auctionRepository, times(1)).findAll();
	}

	@Test
	void testSearchByKeyword() {

		String keyword = "Test";
		List<AuctionItem> expectedItems = new ArrayList<>();
		expectedItems.add(forwardItem);

		when(auctionRepository.findByItemNameContainingIgnoreCase(keyword)).thenReturn(expectedItems);

		List<AuctionItem> actualItems = auctionService.searchByKeyword(keyword);

		assertEquals(expectedItems.size(), actualItems.size());
		assertEquals(expectedItems, actualItems);
		verify(auctionRepository, times(1)).findByItemNameContainingIgnoreCase(keyword);
	}

	@Test
	void testGetAuctionItemByID_Found() {

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(forwardItem));

		AuctionItem actualItem = auctionService.getAuctionItemByID(itemId);

		assertEquals(forwardItem, actualItem);
		verify(auctionRepository, times(1)).findByItemID(itemId);
	}

	@Test
	void testGetAuctionItemByID_NotFound() {

		UUID nonExistentId = UUID.randomUUID();
		when(auctionRepository.findByItemID(nonExistentId)).thenReturn(Optional.empty());

		AuctionItem result = auctionService.getAuctionItemByID(nonExistentId);

		assertNull(result);
		verify(auctionRepository, times(1)).findByItemID(nonExistentId);
	}

	@Test
	void testCreateForwardItem() {

		ForwardItemDTO forwardItemDTO = mock(ForwardItemDTO.class);
		String itemName = "New Forward Item";
		Double currentPrice = 150.0;
		Integer shippingTime = 7;
		LocalDateTime endTime = LocalDateTime.now().plusDays(10);

		when(forwardItemDTO.getItemName()).thenReturn(itemName);
		when(forwardItemDTO.getCurrentPrice()).thenReturn(currentPrice);
		when(forwardItemDTO.getShippingTime()).thenReturn(shippingTime);
		when(forwardItemDTO.getEndTime()).thenReturn(endTime);

		ForwardAuctionItem newItem = new ForwardAuctionItem();
		newItem.setItemName(itemName);
		newItem.setCurrentPrice(currentPrice);
		newItem.setShippingTime(shippingTime);
		newItem.setEndTime(endTime);
		newItem.setSeller(testUser);
		newItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		when(auctionRepository.save(any(ForwardAuctionItem.class))).thenReturn(newItem);

		AuctionItem createdItem = auctionService.createForwardItem(forwardItemDTO, testUser);

		assertEquals(itemName, createdItem.getItemName());
		assertEquals(currentPrice, createdItem.getCurrentPrice());
		assertEquals(shippingTime, createdItem.getShippingTime());
		assertEquals(testUser, createdItem.getSeller());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, createdItem.getAuctionStatus());
		verify(auctionRepository, times(1)).save(any(ForwardAuctionItem.class));
	}

	@Test
	void testCreateDutchItem() {

		DutchItemDTO dutchItemDTO = mock(DutchItemDTO.class);
		String itemName = "New Dutch Item";
		Double currentPrice = 250.0;
		Integer shippingTime = 7;
		Double reservePrice = 150.0;

		when(dutchItemDTO.getItemName()).thenReturn(itemName);
		when(dutchItemDTO.getCurrentPrice()).thenReturn(currentPrice);
		when(dutchItemDTO.getShippingTime()).thenReturn(shippingTime);
		when(dutchItemDTO.getReservePrice()).thenReturn(reservePrice);

		DutchAuctionItem newItem = new DutchAuctionItem();
		newItem.setItemName(itemName);
		newItem.setCurrentPrice(currentPrice);
		newItem.setShippingTime(shippingTime);
		newItem.setReservePrice(reservePrice);
		newItem.setSeller(testUser);
		newItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		when(auctionRepository.save(any(DutchAuctionItem.class))).thenReturn(newItem);

		AuctionItem createdItem = auctionService.createDutchItem(dutchItemDTO, testUser);

		assertEquals(itemName, createdItem.getItemName());
		assertEquals(currentPrice, createdItem.getCurrentPrice());
		assertEquals(shippingTime, createdItem.getShippingTime());
		assertEquals(testUser, createdItem.getSeller());
		assertEquals(AuctionItem.AuctionStatus.AVAILABLE, createdItem.getAuctionStatus());
		verify(auctionRepository, times(1)).save(any(DutchAuctionItem.class));
	}

	@Test
	void testDecreaseDutchPrice() {

		Double decreaseBy = 50.0;
		Double newPrice = dutchItem.getCurrentPrice() - decreaseBy;

		when(auctionRepository.findByItemID(itemId)).thenReturn(Optional.of(dutchItem));
		when(auctionRepository.save(dutchItem)).thenReturn(dutchItem);

		AuctionItem updatedItem = auctionService.decreaseDutchPrice(itemId, userId, decreaseBy);

		assertEquals(newPrice, updatedItem.getCurrentPrice());
		verify(auctionRepository, times(1)).findByItemID(itemId);
		verify(auctionRepository, times(1)).save(dutchItem);
	}

	@Test
	void testGetAvailableAuctionItems() {

		List<AuctionItem> expectedItems = new ArrayList<>();
		expectedItems.add(forwardItem);

		when(auctionRepository.findByAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE)).thenReturn(expectedItems);

		List<AuctionItem> actualItems = auctionService.getAvailableAuctionItems();

		assertEquals(expectedItems.size(), actualItems.size());
		assertEquals(expectedItems, actualItems);
		verify(auctionRepository, times(1)).findByAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);
	}

	@Test
	void testSearchAvailableByKeyword() {

		String keyword = "Test";
		List<AuctionItem> expectedItems = new ArrayList<>();
		expectedItems.add(forwardItem);

		when(auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus(keyword,
				AuctionItem.AuctionStatus.AVAILABLE)).thenReturn(expectedItems);

		List<AuctionItem> actualItems = auctionService.searchAvailableByKeyword(keyword);

		assertEquals(expectedItems.size(), actualItems.size());
		assertEquals(expectedItems, actualItems);
		verify(auctionRepository, times(1)).findByItemNameContainingIgnoreCaseAndAuctionStatus(keyword,
				AuctionItem.AuctionStatus.AVAILABLE);
	}
}