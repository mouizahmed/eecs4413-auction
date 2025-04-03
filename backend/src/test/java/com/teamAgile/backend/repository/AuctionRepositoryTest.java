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
        // Arrange
        User seller = createTestUser("seller");
        AuctionItem item = createTestAuctionItem("Test Item", 100.0, seller);
        UUID itemId = item.getItemID();

        // Act
        Optional<AuctionItem> foundItem = auctionRepository.findByItemID(itemId);

        // Assert
        assertTrue(foundItem.isPresent());
        assertEquals(itemId, foundItem.get().getItemID());
        assertEquals("Test Item", foundItem.get().getItemName());
    }

    @Test
    void findByItemName_ReturnsAuctionItem() {
        // Arrange
        User seller = createTestUser("seller");
        createTestAuctionItem("Test Item", 100.0, seller);

        // Act
        Optional<AuctionItem> foundItem = auctionRepository.findByItemName("Test Item");

        // Assert
        assertTrue(foundItem.isPresent());
        assertEquals("Test Item", foundItem.get().getItemName());
    }

    @Test
    void findAll_ReturnsAllAuctionItems() {
        // Arrange
        User seller = createTestUser("seller");
        createTestAuctionItem("Item 1", 100.0, seller);
        createTestAuctionItem("Item 2", 200.0, seller);

        // Act
        List<AuctionItem> items = auctionRepository.findAll();

        // Assert
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Item 1")));
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Item 2")));
    }

    @Test
    void findByItemNameContainingIgnoreCase_ReturnsMatchingItems() {
        // Arrange
        User seller = createTestUser("seller");
        createTestAuctionItem("Test Item", 100.0, seller);
        createTestAuctionItem("Another Test", 200.0, seller);
        createTestAuctionItem("Not Matching", 300.0, seller);

        // Act
        List<AuctionItem> items = auctionRepository.findByItemNameContainingIgnoreCase("test");

        // Assert
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Test Item")));
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("Another Test")));
    }

    @Test
    void findByItemNameContainingIgnoreCaseAndAuctionStatus_ReturnsMatchingItems() {
        // Arrange
        User seller = createTestUser("seller");
        AuctionItem item1 = createTestAuctionItem("Test Item", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Another Test", 200.0, seller);
        createTestAuctionItem("Not Matching", 300.0, seller);

        // Change status of one item
        item2.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item2);

        // Act
        List<AuctionItem> availableItems = auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus("test",
                AuctionStatus.AVAILABLE);
        List<AuctionItem> soldItems = auctionRepository.findByItemNameContainingIgnoreCaseAndAuctionStatus("test",
                AuctionStatus.SOLD);

        // Assert
        assertEquals(1, availableItems.size());
        assertEquals("Test Item", availableItems.get(0).getItemName());

        assertEquals(1, soldItems.size());
        assertEquals("Another Test", soldItems.get(0).getItemName());
    }

    @Test
    void findByHighestBidderAndAuctionStatus_ReturnsMatchingItems() {
        // Arrange
        User seller = createTestUser("seller");
        User bidder = createTestUser("bidder");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        // Place bid on item1
        Bid bid = new Bid(item1.getItemID(), bidder, 120.0);
        entityManager.persistAndFlush(bid);

        // Update item1 to reflect bid
        item1.setHighestBidder(bidder);
        item1.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item1);

        // Act
        List<AuctionItem> items = auctionRepository.findByHighestBidderAndAuctionStatus(bidder, AuctionStatus.SOLD);

        // Assert
        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getItemName());
        assertEquals(bidder.getUserID(), items.get(0).getHighestBidder().getUserID());
    }

    @Test
    void findByAuctionStatus_ReturnsMatchingItems() {
        // Arrange
        User seller = createTestUser("seller");
        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        // Change status of one item
        item2.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item2);

        // Act
        List<AuctionItem> availableItems = auctionRepository.findByAuctionStatus(AuctionStatus.AVAILABLE);
        List<AuctionItem> soldItems = auctionRepository.findByAuctionStatus(AuctionStatus.SOLD);

        // Assert
        assertEquals(1, availableItems.size());
        assertEquals("Item 1", availableItems.get(0).getItemName());

        assertEquals(1, soldItems.size());
        assertEquals("Item 2", soldItems.get(0).getItemName());
    }

    @Test
    void findByUserBidsAndStatus_ReturnsMatchingItems() {
        // Arrange
        User seller = createTestUser("seller");
        User bidder = createTestUser("bidder");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        // Place bids
        Bid bid1 = new Bid(item1.getItemID(), bidder, 120.0);
        bidRepository.save(bid1);

        // Set status on item1
        item1.setAuctionStatus(AuctionStatus.SOLD);
        entityManager.persistAndFlush(item1);

        // Act
        List<AuctionItem> items = auctionRepository.findByUserBidsAndStatus(bidder, AuctionStatus.SOLD);

        // Assert
        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getItemName());
    }

    @Test
    void findBySeller_ReturnsMatchingItems() {
        // Arrange
        User seller1 = createTestUser("seller1");
        User seller2 = createTestUser("seller2");

        createTestAuctionItem("Item 1", 100.0, seller1);
        createTestAuctionItem("Item 2", 200.0, seller1);
        createTestAuctionItem("Item 3", 300.0, seller2);

        // Act
        List<AuctionItem> seller1Items = auctionRepository.findBySeller(seller1);
        List<AuctionItem> seller2Items = auctionRepository.findBySeller(seller2);

        // Assert
        assertEquals(2, seller1Items.size());
        assertTrue(seller1Items.stream().anyMatch(item -> item.getItemName().equals("Item 1")));
        assertTrue(seller1Items.stream().anyMatch(item -> item.getItemName().equals("Item 2")));

        assertEquals(1, seller2Items.size());
        assertEquals("Item 3", seller2Items.get(0).getItemName());
    }

    // Helper methods to create test entities

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setSecurityQuestion("What is your favorite color?");
        user.setSecurityAnswer("blue");

        // Create and set address
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