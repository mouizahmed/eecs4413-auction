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
import com.teamAgile.backend.model.Bid;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.ForwardAuctionItem;

@DataJpaTest
public class BidRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BidRepository bidRepository;

    @Test
    void findByUser_UserID_ReturnsUserBids() {
        // Arrange
        User bidder = createTestUser("bidder");
        UUID bidderId = bidder.getUserID();

        User seller = createTestUser("seller");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        Bid bid1 = createTestBid(item1.getItemID(), bidder, 120.0);
        Bid bid2 = createTestBid(item2.getItemID(), bidder, 220.0);

        User otherBidder = createTestUser("otherBidder");
        createTestBid(item1.getItemID(), otherBidder, 130.0);

        List<Bid> foundBids = bidRepository.findByUser_UserID(bidderId);

        assertEquals(2, foundBids.size());
        assertTrue(foundBids.stream().anyMatch(b -> b.getBidID().equals(bid1.getBidID())));
        assertTrue(foundBids.stream().anyMatch(b -> b.getBidID().equals(bid2.getBidID())));
    }

    @Test
    void findByItemIDOrderByBidAmountDesc_ReturnsBidsInDescendingOrder() {
        User bidder1 = createTestUser("bidder1");
        User bidder2 = createTestUser("bidder2");
        User bidder3 = createTestUser("bidder3");

        User seller = createTestUser("seller");

        AuctionItem item = createTestAuctionItem("Test Item", 100.0, seller);
        UUID itemId = item.getItemID();

        Bid lowBid = createTestBid(itemId, bidder1, 110.0);
        Bid mediumBid = createTestBid(itemId, bidder2, 120.0);
        Bid highBid = createTestBid(itemId, bidder3, 130.0);

        List<Bid> bids = bidRepository.findByItemIDOrderByBidAmountDesc(itemId);

        assertEquals(3, bids.size());
        assertEquals(highBid.getBidID(), bids.get(0).getBidID());
        assertEquals(mediumBid.getBidID(), bids.get(1).getBidID());
        assertEquals(lowBid.getBidID(), bids.get(2).getBidID());
    }

    @Test
    void findByBidID_ReturnsBid() {
        User bidder = createTestUser("bidder");
        User seller = createTestUser("seller");

        AuctionItem item = createTestAuctionItem("Test Item", 100.0, seller);

        Bid bid = createTestBid(item.getItemID(), bidder, 120.0);
        UUID bidId = bid.getBidID();

        Optional<Bid> foundBid = bidRepository.findByBidID(bidId);

        assertTrue(foundBid.isPresent());
        assertEquals(bidId, foundBid.get().getBidID());
        assertEquals(bidder.getUserID(), foundBid.get().getUser().getUserID());
        assertEquals(120.0, foundBid.get().getBidAmount());
    }

    @Test
    void findByItemID_ReturnsBid() {
        User bidder = createTestUser("bidder");
        User seller = createTestUser("seller");

        AuctionItem item = createTestAuctionItem("Test Item", 100.0, seller);
        UUID itemId = item.getItemID();

        Bid bid = createTestBid(itemId, bidder, 120.0);

        Optional<Bid> foundBid = bidRepository.findByItemID(itemId);

        assertTrue(foundBid.isPresent());
        assertEquals(bid.getBidID(), foundBid.get().getBidID());
        assertEquals(itemId, foundBid.get().getItemID());
    }

    @Test
    void findAll_ReturnsAllBids() {
        User bidder = createTestUser("bidder");
        User seller = createTestUser("seller");

        AuctionItem item1 = createTestAuctionItem("Item 1", 100.0, seller);
        AuctionItem item2 = createTestAuctionItem("Item 2", 200.0, seller);

        createTestBid(item1.getItemID(), bidder, 120.0);
        createTestBid(item2.getItemID(), bidder, 220.0);

        List<Bid> allBids = bidRepository.findAll();

        assertEquals(2, allBids.size());
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

    private Bid createTestBid(UUID itemId, User bidder, double amount) {
        Bid bid = new Bid(itemId, bidder, amount);
        return entityManager.persistAndFlush(bid);
    }
}