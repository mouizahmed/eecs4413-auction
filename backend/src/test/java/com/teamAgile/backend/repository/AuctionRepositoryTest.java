package com.teamAgile.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.teamAgile.backend.model.Address;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.ForwardAuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.model.AuctionItem.AuctionStatus;

@DataJpaTest
public class AuctionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Test
    void findByItemID_ReturnsAuctionItem() {
        User seller = createTestUser("seller");
        AuctionItem item = createTestAuctionItem("Test Item", 100.0, seller);
        UUID itemId = item.getItemID();

        Optional<AuctionItem> foundItem = auctionRepository.findByItemID(itemId);

        assertTrue(foundItem.isPresent());
        assertEquals(itemId, foundItem.get().getItemID());
        assertEquals("Test Item", foundItem.get().getItemName());
    }

    @Test
    void findByItemName_ReturnsAuctionItem() {
        User seller = createTestUser("seller");
        createTestAuctionItem("Test Item", 100.0, seller);

        Optional<AuctionItem> foundItem = auctionRepository.findByItemName("Test Item");

        assertTrue(foundItem.isPresent());
        assertEquals("Test Item", foundItem.get().getItemName());
    }

    @Test
    void findAll_ReturnsAllAuctionItems() {
        User seller = createTestUser("seller");
        createTestAuctionItem("Item 1", 100.0, seller);
        createTestAuctionItem("Item 2", 200.0, seller);

        List<AuctionItem> items = auctionRepository.findAll();

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Item 1")));
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Item 2")));
    }

    @Test
    void findByItemNameContainingIgnoreCase_ReturnsMatchingItems() {
        User seller = createTestUser("seller");
        createTestAuctionItem("Test Item", 100.0, seller);
        createTestAuctionItem("Another Test", 200.0, seller);
        createTestAuctionItem("Not Matching", 300.0, seller);

        List<AuctionItem> items = auctionRepository.findByItemNameContainingIgnoreCase("test");

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Test Item")));
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Another Test")));
    }

    @Test
    void findByItemNameContainingIgnoreCaseAndAuctionStatus_ReturnsMatchingItems() {
        User seller = createTestUser("seller");
        AuctionItem item1 = createTestAuctionItem("Test Item", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Another Test", 200.0, seller);
        createTestAuctionItem("Not Matching", 300.0, seller);

        item2.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item2);

        List<AuctionItem> availableItems = auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus("test",
                AuctionStatus.AVAILABLE);
        List<AuctionItem> soldItems = auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus("test",
                AuctionStatus.SOLD);

        assertEquals(1, availableItems.size());
        assertEquals("Test Item", availableItems.get(0).getItemName());

        assertEquals(1, soldItems.size());
        assertEquals("Another Test", soldItems.get(0).getItemName());
    }

    @Test
    void findByHighestBidderAndAuctionStatus_ReturnsMatchingItems() {
        User seller = createTestUser("seller");
        User bidder = createTestUser("bidder");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);

        Bid bid = new Bid(item1.getItemID(), bidder, 120.0);
        entityManager.persistAndFlush(bid);

        item1.setHighestBidder(bidder);
        item1.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item1);

        List<AuctionItem> items = auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);

        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getItemName());
        assertEquals(bidder.getUserID(), items.get(0).getHighestBidder().getUserID());
    }

    @Test
    void findByAuctionStatus_ReturnsMatchingItems() {
        User seller = createTestUser("seller");
        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        item2.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item2);

        List<AuctionItem> availableItems = auctionRepository.findByAuctionStatus(AuctionStatus.AVAILABLE);
        List<AuctionItem> soldItems = auctionRepository.findByAuctionStatus(AuctionStatus.SOLD);

        assertEquals(1, availableItems.size());
        assertEquals("Item 1", availableItems.get(0).getItemName());

        assertEquals(1, soldItems.size());
        assertEquals("Item 2", soldItems.get(0).getItemName());
    }

    @Test
    void findByUserBidsAndStatus_ReturnsMatchingItems() {
        User seller = createTestUser("seller");
        User bidder = createTestUser("bidder");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);

        Bid bid1 = new Bid(item1.getItemID(), bidder, 120.0);
        bidRepository.save(bid1);

        item1.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item1);

        List<AuctionItem> items = auctionRepository.findByUserBidsAndStatus(bidder, AuctionStatus.SOLD);

        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getItemName());
    }

    @Test
    void findBySeller_ReturnsMatchingItems() {
        User seller1 = createTestUser("seller1");
        User seller2 = createTestUser("seller2");

        createTestAuctionItem("Item 1", 100.0, seller1);
        createTestAuctionItem("Item 2", 200.0, seller1);
        createTestAuctionItem("Item 3", 300.0, seller2);

        List<AuctionItem> seller1Items = auctionRepository.findBySeller(seller1);
        List<AuctionItem> seller2Items = auctionRepository.findBySeller(seller2);

        assertEquals(2, seller1Items.size());
        assertTrue(seller1Items.stream().anyMatch(item -> item.getItemName().equals("Item 1")));
        assertTrue(seller1Items.stream().anyMatch(item -> item.getItemName().equals("Item 2")));

        assertEquals(1, seller2Items.size());
        assertEquals("Item 3", seller2Items.get(0).getItemName());
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setSecurityQuestion("What is your favorite color?");
        user.setSecurityAnswer("blue");

        Address address = new Address("Main St", 123, "A1B2C3", "City", "Province", "Country");
        user.setAddress(address);

        return entityManager.persistAndFlush(user);
    }

    private AuctionItem createTestAuctionItem(String name, double price, User seller) {
        ForwardAuctionItem item = new ForwardAuctionItem();
        item.setItemName(name);
        item.setCurrentPrice(price);
        item.setSeller(seller);
        item.setShippingTime(5);
        item.setEndTime(LocalDateTime.now().plusDays(7));

        return entityManager.persistAndFlush(item);
    }
}