package com.teamAgile.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.teamAgile.backend.DTO.ApiResponse;
import com.teamAgile.backend.DTO.AuctionItemResponseDTO;
import com.teamAgile.backend.DTO.BidResponseDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.DTO.hateoas.AuctionItemModel;
import com.teamAgile.backend.DTO.hateoas.AuctionItemModelAssembler;
import com.teamAgile.backend.DTO.hateoas.BidModel;
import com.teamAgile.backend.DTO.hateoas.BidModelAssembler;
import com.teamAgile.backend.DTO.hateoas.ReceiptModelAssembler;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.BidService;
import com.teamAgile.backend.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class AuctionControllerTest {

    @Mock
    private AuctionService auctionService;

    @Mock
    private BidService bidService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AuctionItemModelAssembler auctionItemModelAssembler;

    @Mock
    private BidModelAssembler bidModelAssembler;

    @Mock
    private ReceiptModelAssembler receiptModelAssembler;

    @Mock
    private HttpServletRequest request;

    @Spy
    @InjectMocks
    private AuctionController auctionController;

    private UUID itemId;
    private AuctionItem auctionItem;
    private User testUser;
    private List<AuctionItem> auctionItems;
    private AuctionItemResponseDTO responseDTO;
    private AuctionItemModel itemModel;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserID(UUID.randomUUID());
        testUser.setUsername("testuser");

        itemId = UUID.randomUUID();
        auctionItem = mock(ForwardAuctionItem.class);
        lenient().when(auctionItem.getItemID()).thenReturn(itemId);
        lenient().when(auctionItem.getItemName()).thenReturn("Test Item");
        lenient().when(auctionItem.getCurrentPrice()).thenReturn(100.0);
        lenient().when(auctionItem.getSeller()).thenReturn(testUser);

        auctionItems = new ArrayList<>();
        auctionItems.add(auctionItem);

        responseDTO = mock(AuctionItemResponseDTO.class);
        itemModel = mock(AuctionItemModel.class);
    }

    @Test
    void getAllAuctionItems_Success() {
        when(auctionService.getAvailableAuctionItems()).thenReturn(auctionItems);
        when(auctionItemModelAssembler.toModel(any(AuctionItemResponseDTO.class))).thenReturn(itemModel);

        List<AuctionItemModel> itemModels = new ArrayList<>();
        itemModels.add(itemModel);
        CollectionModel<AuctionItemModel> collectionModel = CollectionModel.of(itemModels);

        try (MockedStatic<AuctionItemResponseDTO> mockedStatic = mockStatic(AuctionItemResponseDTO.class)) {
            mockedStatic.when(() -> AuctionItemResponseDTO.fromAuctionItem(any(AuctionItem.class)))
                    .thenReturn(responseDTO);

            ResponseEntity<ApiResponse<CollectionModel<AuctionItemModel>>> response = auctionController
                    .getAllAuctionItems();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isSuccess());

            verify(auctionService).getAvailableAuctionItems();
        }
    }

    @Test
    void getAuctionItemByID_Success() {
        when(auctionService.getAuctionItemWithBidsByID(itemId)).thenReturn(auctionItem);
        when(auctionItemModelAssembler.toModel(any(AuctionItemResponseDTO.class))).thenReturn(itemModel);

        try (MockedStatic<AuctionItemResponseDTO> mockedStatic = mockStatic(AuctionItemResponseDTO.class)) {
            mockedStatic.when(() -> AuctionItemResponseDTO.fromAuctionItem(any(AuctionItem.class)))
                    .thenReturn(responseDTO);
            mockedStatic.when(() -> AuctionItemResponseDTO.fromAuctionItem(any(AuctionItem.class), any()))
                    .thenReturn(responseDTO);

            ResponseEntity<ApiResponse<AuctionItemModel>> response = auctionController.getAuctionItemByID(itemId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isSuccess());

            verify(auctionService).getAuctionItemWithBidsByID(itemId);
        }
    }

    @Test
    void getAuctionItemByID_NotFound() {
        when(auctionService.getAuctionItemWithBidsByID(itemId)).thenReturn(null);

        ResponseEntity<ApiResponse<AuctionItemModel>> response = auctionController.getAuctionItemByID(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void uploadForwardAuctionItem_Success() {
        ForwardItemDTO itemDTO = new ForwardItemDTO();
        itemDTO.setItemName("New Item");
        itemDTO.setCurrentPrice(150.0);
        itemDTO.setEndTime(LocalDateTime.now().plusDays(7));


        doReturn(testUser).when(auctionController).getCurrentUser(any(HttpServletRequest.class));

        when(auctionService.createForwardItem(any(ForwardItemDTO.class), eq(testUser))).thenReturn(auctionItem);

        try (MockedStatic<AuctionItemResponseDTO> mockedStatic = mockStatic(AuctionItemResponseDTO.class)) {
            mockedStatic.when(() -> AuctionItemResponseDTO.fromAuctionItem(any(AuctionItem.class)))
                    .thenReturn(responseDTO);

            ResponseEntity<ApiResponse<AuctionItemResponseDTO>> response = auctionController
                    .uploadForwardAuctionItem(itemDTO, request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isSuccess());

            verify(auctionService).createForwardItem(any(ForwardItemDTO.class), eq(testUser));
        }
    }

    @Test
    void uploadForwardAuctionItem_Unauthorized() {
        ForwardItemDTO itemDTO = new ForwardItemDTO();
        itemDTO.setItemName("New Item");
        itemDTO.setCurrentPrice(150.0);
        itemDTO.setEndTime(LocalDateTime.now().plusDays(7));

        doReturn(null).when(auctionController).getCurrentUser(any(HttpServletRequest.class));

        ResponseEntity<ApiResponse<AuctionItemResponseDTO>> response = auctionController
                .uploadForwardAuctionItem(itemDTO, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        verify(auctionService, never()).createForwardItem(any(), any());
    }

    @Test
    void uploadForwardAuctionItem_MissingData() {
        ForwardItemDTO itemDTO = new ForwardItemDTO();
        itemDTO.setCurrentPrice(150.0);
        itemDTO.setEndTime(LocalDateTime.now().plusDays(7));

        doReturn(testUser).when(auctionController).getCurrentUser(any(HttpServletRequest.class));

        ResponseEntity<ApiResponse<AuctionItemResponseDTO>> response = auctionController
                .uploadForwardAuctionItem(itemDTO, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void placeBid_Success() {
        String itemIdStr = itemId.toString();
        String bidAmount = "200.0";
        Bid bid = mock(Bid.class);
        BidResponseDTO bidResponseDTO = mock(BidResponseDTO.class);

        doReturn(testUser).when(auctionController).getCurrentUser(any(HttpServletRequest.class));
        when(bidService.createBid(eq(itemId), eq(testUser), eq(200.0))).thenReturn(bid);

        ResponseEntity<ApiResponse<BidResponseDTO>> response = auctionController.placeBid(itemIdStr, bidAmount,
                request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void placeBid_Unauthorized() {
        String itemIdStr = itemId.toString();
        String bidAmount = "200.0";

        doReturn(null).when(auctionController).getCurrentUser(any(HttpServletRequest.class));

        ResponseEntity<ApiResponse<BidResponseDTO>> response = auctionController.placeBid(itemIdStr, bidAmount,
                request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        verify(bidService, never()).createBid(any(), any(), anyDouble());
    }
}