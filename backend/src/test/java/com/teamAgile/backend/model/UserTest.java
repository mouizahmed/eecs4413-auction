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
        assertNotEquals("password123", user.getPassword());
        assertTrue(BCryptHashing.checkPassword("password123", user.getPassword()));

        user.setPassword("newpassword123");
        assertNotEquals("newpassword123", user.getPassword());
        assertTrue(BCryptHashing.checkPassword("newpassword123", user.getPassword()));

        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("");
        });
    }

    @Test
    void testSecurityQuestionHandling() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityQuestion(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityQuestion("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityAnswer(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setSecurityAnswer("");
        });

        assertTrue(user.checkSecurityAnswer("Smith"));
        assertFalse(user.checkSecurityAnswer("wronganswer"));

        user.setSecurityAnswer("NewAnswer");
        assertTrue(user.checkSecurityAnswer("NewAnswer"));
        assertFalse(user.checkSecurityAnswer("Smith"));
    }

    @Test
    void testUserRoles() {
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(1, user.getAuthorities().size());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testBidManagement() {
        AuctionItem auctionItem = new ForwardAuctionItem("Test Item", user, AuctionItem.AuctionStatus.AVAILABLE, 100.0,
                5,
                LocalDateTime.now().plusDays(7));
        user.addAuctionItem(auctionItem);

        Bid bid = new Bid(UUID.randomUUID(), user, 100.0);

        user.addBid(bid);
        assertTrue(user.getBids().contains(bid));
        assertEquals(user, bid.getUser());

        user.removeBid(bid);
        assertFalse(user.getBids().contains(bid));
        assertNull(bid.getUser());

        assertThrows(IllegalArgumentException.class, () -> {
            user.addBid(null);
        });
    }

    @Test
    void testAuctionManagement() {
        ForwardAuctionItem forwardAuction = new ForwardAuctionItem();
        DutchAuctionItem dutchAuction = new DutchAuctionItem();

        // Test auction addition
        user.addAuctionItem(forwardAuction);
        user.addAuctionItem(dutchAuction);
        assertTrue(user.getAuctionItems().contains(forwardAuction));
        assertTrue(user.getAuctionItems().contains(dutchAuction));
        assertEquals(user, forwardAuction.getSeller());
        assertEquals(user, dutchAuction.getSeller());

        user.removeAuctionItem(forwardAuction);
        assertFalse(user.getAuctionItems().contains(forwardAuction));
        assertNull(forwardAuction.getSeller());

        assertThrows(IllegalArgumentException.class, () -> {
            user.addAuctionItem(null);
        });
    }

    @Test
    void testReceiptManagement() {
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

        assertTrue(user.getReceipts().contains(receipt));
        assertEquals(user, receipt.getUser());

        user.removeReceipt(receipt);
        assertFalse(user.getReceipts().contains(receipt));
        assertNull(receipt.getUser());

        assertThrows(IllegalArgumentException.class, () -> {
            user.addReceipt(null);
        });
    }

    @Test
    void testAddressManagement() {
        Address newAddress = new Address();
        newAddress.setStreetName("New Street");
        newAddress.setStreetNum(456);
        newAddress.setPostalCode("B2B2B2");
        newAddress.setCity("Vancouver");
        newAddress.setProvince("BC");
        newAddress.setCountry("Canada");

        user.setAddress(newAddress);
        assertEquals(newAddress, user.getAddress());

        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(null);
        });

        Address invalidAddress = new Address();
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(invalidAddress);
        });
    }

    @Test
    void testAddressValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(null);
        });

        Address invalidAddress1 = new Address();
        invalidAddress1.setStreetName("");
        invalidAddress1.setStreetNum(123);
        invalidAddress1.setPostalCode("A1A1A1");
        invalidAddress1.setCity("Test City");
        invalidAddress1.setProvince("Test Province");
        invalidAddress1.setCountry("Test Country");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(invalidAddress1);
        });

        Address invalidAddress2 = new Address();
        invalidAddress2.setStreetName("Test Street");
        invalidAddress2.setStreetNum(-1);
        invalidAddress2.setPostalCode("A1A1A1");
        invalidAddress2.setCity("Test City");
        invalidAddress2.setProvince("Test Province");
        invalidAddress2.setCountry("Test Country");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(invalidAddress2);
        });

        Address invalidAddress3 = new Address();
        invalidAddress3.setStreetName("Test Street");
        invalidAddress3.setStreetNum(123);
        invalidAddress3.setPostalCode("123456");
        invalidAddress3.setCity("Test City");
        invalidAddress3.setProvince("Test Province");
        invalidAddress3.setCountry("Test Country");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setAddress(invalidAddress3);
        });

        Address validAddress = new Address();
        validAddress.setStreetName("Test Street");
        validAddress.setStreetNum(123);
        validAddress.setPostalCode("A1A1A1");
        validAddress.setCity("Test City");
        validAddress.setProvince("Test Province");
        validAddress.setCountry("Test Country");
        user.setAddress(validAddress);
        assertEquals(validAddress, user.getAddress());
    }

    @Test
    void testUsernameUniqueness() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUsername(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setUsername("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setUsername("   ");
        });

        String validUsername = "testuser";
        user.setUsername(validUsername);
        assertEquals(validUsername, user.getUsername());
    }

    @Test
    void testPasswordValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("   ");
        });

        String validPassword = "newpassword123";
        user.setPassword(validPassword);

        assertNotEquals(validPassword, user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$"));
        assertEquals(60, user.getPassword().length());
    }
}