package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.time.YearMonth;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.util.BCryptHashing;
import java.time.LocalDateTime;

class UserTest {
    private User user;
    private SignUpDTO signUpDTO;

    @BeforeEach
    void setUp() {
        signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setUsername("johndoe");
        signUpDTO.setPassword("password123");
        signUpDTO.setSecurityQuestion("What is your mother's maiden name?");
        signUpDTO.setSecurityAnswer("Smith");
        signUpDTO.setStreetName("Main Street");
        signUpDTO.setStreetNum(123);
        signUpDTO.setPostalCode("A1A1A1");
        signUpDTO.setCity("Toronto");
        signUpDTO.setProvince("Ontario");
        signUpDTO.setCountry("Canada");

        user = new User(signUpDTO);
    }

    @Test
    void testPasswordHashing() {
        // Test that password is hashed during creation
        assertNotEquals("password123", user.getPassword());
        assertTrue(BCryptHashing.checkPassword("password123", user.getPassword()));

        // Test password update
        user.setPassword("newpassword123");
        assertNotEquals("newpassword123", user.getPassword());
        assertTrue(BCryptHashing.checkPassword("newpassword123", user.getPassword()));

        // Test null password
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword(null);
        });

        // Test empty password
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("");
        });
    }

    @Test
    void testSecurityQuestionHandling() {
        // Test security question validation
        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityQuestion(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityQuestion("");
        });

        // Test security answer validation
        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityAnswer(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityAnswer("");
        });

        // Test security answer verification with original answer from setUp
        assertTrue(user.checkSecurityAnswer("Smith"));
        assertFalse(user.checkSecurityAnswer("wronganswer"));

        // Test setting a new security answer
        user.setSecurityAnswer("NewAnswer");
        assertTrue(user.checkSecurityAnswer("NewAnswer"));
        assertFalse(user.checkSecurityAnswer("Smith")); // Old answer should no longer work
    }

    @Test
    void testUserRoles() {
        // Test default role assignment
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(1, user.getAuthorities().size());

        // Test user account status
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testBidManagement() {
        // Create test auction item and bid
        AuctionItem auctionItem = new ForwardAuctionItem("Test Item", user, AuctionItem.AuctionStatus.AVAILABLE, 100.0,
                5,
                LocalDateTime.now().plusDays(7));
        user.addAuctionItem(auctionItem);

        // Create a bid using a random UUID since we can't set the item ID directly
        Bid bid = new Bid(UUID.randomUUID(), user, 100.0);

        // Test bid addition
        user.addBid(bid);
        assertTrue(user.getBids().contains(bid));
        assertEquals(user, bid.getUser());

        // Test bid removal
        user.removeBid(bid);
        assertFalse(user.getBids().contains(bid));
        assertNull(bid.getUser());

        // Test null bid
        assertThrows(IllegalArgumentException.class, () -> {
            user.addBid(null);
        });
    }

    @Test
    void testAuctionManagement() {
        // Create test auction items
        ForwardAuctionItem forwardAuction = new ForwardAuctionItem();
        DutchAuctionItem dutchAuction = new DutchAuctionItem();

        // Test auction addition
        user.addAuctionItem(forwardAuction);
        user.addAuctionItem(dutchAuction);
        assertTrue(user.getAuctionItems().contains(forwardAuction));
        assertTrue(user.getAuctionItems().contains(dutchAuction));
        assertEquals(user, forwardAuction.getSeller());
        assertEquals(user, dutchAuction.getSeller());

        // Test auction removal
        user.removeAuctionItem(forwardAuction);
        assertFalse(user.getAuctionItems().contains(forwardAuction));
        assertNull(forwardAuction.getSeller());

        // Test null auction
        assertThrows(IllegalArgumentException.class, () -> {
            user.addAuctionItem(null);
        });
    }

    @Test
    void testReceiptManagement() {
        // Create test receipt
        CreditCard creditCard = new CreditCard();
        creditCard.setCardNum("4111111111111111");
        creditCard.setCardName("John Doe");
        creditCard.setExpDate(YearMonth.of(2025, 12));
        creditCard.setSecurityCode("123");

        Address address = new Address();
        address.setStreetName("Main Street");
        address.setStreetNum(123);
        address.setPostalCode("A1A1A1");
        address.setCity("Toronto");
        address.setProvince("Ontario");
        address.setCountry("Canada");

        Receipt receipt = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 5);

        // Test receipt addition
        assertTrue(user.getReceipts().contains(receipt));
        assertEquals(user, receipt.getUser());

        // Test receipt removal
        user.removeReceipt(receipt);
        assertFalse(user.getReceipts().contains(receipt));
        assertNull(receipt.getUser());

        // Test null receipt
        assertThrows(IllegalArgumentException.class, () -> {
            user.addReceipt(null);
        });
    }

    @Test
    void testAddressManagement() {
        // Test address update
        Address newAddress = new Address();
        newAddress.setStreetName("New Street");
        newAddress.setStreetNum(456);
        newAddress.setPostalCode("B2B2B2");
        newAddress.setCity("Vancouver");
        newAddress.setProvince("BC");
        newAddress.setCountry("Canada");

        user.setAddress(newAddress);
        assertEquals(newAddress, user.getAddress());

        // Test null address
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(null);
        });

        // Test invalid address
        Address invalidAddress = new Address();
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(invalidAddress);
        });
    }
}