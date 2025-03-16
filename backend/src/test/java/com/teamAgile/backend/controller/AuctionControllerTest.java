package com.teamAgile.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamAgile.backend.DTO.DutchItemDTO;
import com.teamAgile.backend.DTO.ForwardItemDTO;
import com.teamAgile.backend.DTO.hateoas.AuctionItemModelAssembler;
import com.teamAgile.backend.DTO.hateoas.BidModelAssembler;
import com.teamAgile.backend.config.WebSocketConfig;
import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.CreditCard;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.BidService;
import com.teamAgile.backend.service.PaymentService;
import com.teamAgile.backend.service.UserService;

@WebMvcTest(controllers = AuctionController.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WebSecurityConfigurer.class,
				WebSocketConfigurer.class, WebSocketConfig.class }) })
public class AuctionControllerTest {

	@Configuration
	static class TestConfig {
		@Bean
		@Primary
		public AuctionController auctionController(AuctionService auctionService, BidService bidService,
				PaymentService paymentService, AuctionItemModelAssembler auctionItemModelAssembler,
				BidModelAssembler bidModelAssembler) {
			return new TestAuctionController(auctionService, bidService, paymentService, auctionItemModelAssembler,
					bidModelAssembler);
		}
	}

	
	static class TestAuctionController extends AuctionController {
		public TestAuctionController(AuctionService auctionService, BidService bidService,
				PaymentService paymentService, AuctionItemModelAssembler auctionItemModelAssembler,
				BidModelAssembler bidModelAssembler) {
			super(auctionService, bidService, paymentService, auctionItemModelAssembler, bidModelAssembler);
		}

		@Override
		protected User getCurrentUser(jakarta.servlet.http.HttpServletRequest request) {
			
			User user = new User();
			user.setUserID(UUID.randomUUID());
			user.setUsername("testUser");
			return user;
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuctionService auctionService;

	@MockBean
	private BidService bidService;

	@MockBean
	private PaymentService paymentService;

	@MockBean
	private UserService userService;

	@MockBean
	private AuctionItemModelAssembler auctionItemModelAssembler;

	@MockBean
	private BidModelAssembler bidModelAssembler;

	private User testUser;
	private ForwardAuctionItem testAuctionItem;
	private Bid testBid;
	private Receipt testReceipt;
	private UUID userId;
	private UUID itemId;
	private UUID bidId;
	private UUID receiptId;

	@BeforeEach
	void setUp() {
		
		userId = UUID.randomUUID();
		testUser = new User();
		testUser.setUserID(userId);
		testUser.setUsername("testUser");
		testUser.setFirstName("Test");
		testUser.setLastName("User");

		
		Address address = new Address("Main St", 123, "12345", "Test City", "Test Country");
		testUser.setAddress(address);

		
		itemId = UUID.randomUUID();
		testAuctionItem = new ForwardAuctionItem();
		testAuctionItem.setItemName("Test Item");
		testAuctionItem.setSeller(testUser);
		testAuctionItem.setCurrentPrice(100.0);
		testAuctionItem.setShippingTime(5);
		testAuctionItem.setEndTime(LocalDateTime.now().plusDays(7));
		testAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.AVAILABLE);

		
		bidId = UUID.randomUUID();
		testBid = new Bid(itemId, testUser, 150.0);

		
		receiptId = UUID.randomUUID();
		CreditCard creditCard = new CreditCard("1234567890123456", "Test User", YearMonth.now().plusYears(1), "123");
		
		testReceipt = new Receipt(itemId, testUser, 150.0, creditCard, address, 5);
	}

	@Test
	@WithMockUser(username = "testUser")
	void testGetAllAuctionItems() throws Exception {
		
		List<AuctionItem> auctionItems = new ArrayList<>();
		auctionItems.add(testAuctionItem);

		when(auctionService.getAvailableAuctionItems()).thenReturn(auctionItems);

		
		mockMvc.perform(get("/auction/get-all").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.message").value("Success"));
	}

	@Test
	@WithMockUser(username = "testUser", roles = { "ADMIN" })
	void testGetAllAuctionItemsAdmin() throws Exception {
		
		List<AuctionItem> auctionItems = new ArrayList<>();
		auctionItems.add(testAuctionItem);

		when(auctionService.getAllAuctionItems()).thenReturn(auctionItems);

		
		mockMvc.perform(get("/auction/admin/get-all-items").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Success"));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testSearchAuctionItems() throws Exception {
		
		List<AuctionItem> auctionItems = new ArrayList<>();
		auctionItems.add(testAuctionItem);

		when(auctionService.searchAvailableByKeyword(anyString())).thenReturn(auctionItems);

		
		mockMvc.perform(get("/auction/search").param("keyword", "Test").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Success"));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testGetAuctionItemByID() throws Exception {
		
		when(auctionService.getAuctionItemWithBidsByID(any(UUID.class))).thenReturn(testAuctionItem);

		
		mockMvc.perform(
				get("/auction/get-by-id").param("itemID", itemId.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Success"));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testUploadForwardAuctionItem() throws Exception {
		
		ForwardItemDTO mockForwardItemDTO = org.mockito.Mockito.mock(ForwardItemDTO.class);
		when(mockForwardItemDTO.getItemName()).thenReturn("Test Forward Item");
		when(mockForwardItemDTO.getCurrentPrice()).thenReturn(100.0);
		when(mockForwardItemDTO.getShippingTime()).thenReturn(5);
		when(mockForwardItemDTO.getEndTime()).thenReturn(LocalDateTime.now().plusDays(7));

		
		String mockJson = "{\"itemName\":\"Test Forward Item\",\"currentPrice\":100.0,\"shippingTime\":5,\"endTime\":\""
				+ LocalDateTime.now().plusDays(7).toString() + "\"}";

		when(userService.getUserById(any())).thenReturn(testUser);
		when(auctionService.createForwardItem(any(), any())).thenReturn(testAuctionItem);

		
		mockMvc.perform(post("/auction/forward/post").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(mockJson)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testUploadDutchAuctionItem() throws Exception {
		
		DutchItemDTO mockDutchItemDTO = org.mockito.Mockito.mock(DutchItemDTO.class);
		when(mockDutchItemDTO.getItemName()).thenReturn("Test Dutch Item");
		when(mockDutchItemDTO.getCurrentPrice()).thenReturn(200.0);
		when(mockDutchItemDTO.getShippingTime()).thenReturn(5);
		when(mockDutchItemDTO.getReservePrice()).thenReturn(100.0);

		String mockJson = "{\"itemName\":\"Test Dutch Item\",\"currentPrice\":200.0,\"shippingTime\":5,\"reservePrice\":100.0}";

		when(userService.getUserById(any())).thenReturn(testUser);
		when(auctionService.createDutchItem(any(), any())).thenReturn(testAuctionItem);

		
		mockMvc.perform(post("/auction/dutch/post").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(mockJson)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testPlaceBid() throws Exception {
		
		when(userService.getUserById(any(UUID.class))).thenReturn(testUser);
		when(bidService.createBid(any(UUID.class), any(User.class), any(Double.class))).thenReturn(testBid);

		
		mockMvc.perform(post("/auction/bid").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("itemID", itemId.toString()).param("bidAmount", "150.0").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testDecreaseDutchPrice() throws Exception {
		
		when(userService.getUserById(any(UUID.class))).thenReturn(testUser);
		when(auctionService.decreaseDutchPrice(any(UUID.class), any(UUID.class), any(Double.class)))
				.thenReturn(testAuctionItem);

		
		mockMvc.perform(patch("/auction/dutch/decreasePrice").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("itemID", itemId.toString()).param("decreaseBy", "50.0").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testProcessPayment() throws Exception {

		testAuctionItem.setHighestBidder(testUser);
		testAuctionItem.setAuctionStatus(AuctionItem.AuctionStatus.SOLD);

		
		when(auctionService.getAuctionItemById(any())).thenReturn(testAuctionItem);

		YearMonth expDate = YearMonth.now().plusYears(1);

		
		String mockJson = "{\"cardNum\":\"4111111111111111\",\"cardName\":\"Test User\",\"expDate\":\""
				+ expDate.toString() + "\",\"securityCode\":\"123\"}";

		when(userService.getUserById(any())).thenReturn(testUser);
		when(paymentService.createPayment(any(), any(), any())).thenReturn(testReceipt);

		
		mockMvc.perform(post("/auction/pay").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("itemID", itemId.toString()).contentType(MediaType.APPLICATION_JSON).content(mockJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testGetAuctionItemById() throws Exception {
		
		when(auctionService.getAuctionItemById(any(UUID.class))).thenReturn(testAuctionItem);

		
		mockMvc.perform(get("/auction/{itemId}", itemId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Success"));
	}

	@Test
	@WithMockUser(username = "testUser")
	void testGetBidsForItem() throws Exception {
		
		List<Bid> bids = new ArrayList<>();
		bids.add(testBid);

		when(bidService.getBidsByItemId(any(UUID.class))).thenReturn(bids);

		
		mockMvc.perform(get("/auction/{itemId}/bids", itemId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Success"));
	}
}
