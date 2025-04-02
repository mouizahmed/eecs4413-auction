package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

class ReceiptTest {
    private Receipt receipt;
    private User user;
    private CreditCard creditCard;
    private Address address;
    private static final double DELTA = 0.001; // for double comparisons

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserID(UUID.randomUUID());

        creditCard = new CreditCard();
        creditCard.setCardNum("4111111111111111");
        creditCard.setCardName("Test User");
        creditCard.setExpDate(YearMonth.of(2025, 12));
        creditCard.setSecurityCode("123");

        address = new Address();
        address.setStreetName("Test Street");
        address.setStreetNum(123);
        address.setPostalCode("A1A1A1");
        address.setCity("Test City");
        address.setProvince("Test Province");
        address.setCountry("Test Country");

        receipt = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 5);
    }

    @Test
    void testReceiptCalculations() {
        // Test total cost calculation with different shipping times
        Receipt fastShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 1);
        Receipt normalShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 5);
        Receipt slowShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 10);

        // Test that total cost is preserved
        assertEquals(100.0, fastShipping.getTotalCost(), DELTA);
        assertEquals(100.0, normalShipping.getTotalCost(), DELTA);
        assertEquals(100.0, slowShipping.getTotalCost(), DELTA);

        // Test handling of decimal precision
        Receipt precisionTest = new Receipt(UUID.randomUUID(), user, 10.99, creditCard, address, 5);
        assertEquals(10.99, precisionTest.getTotalCost(), DELTA);
        assertEquals(2, String.valueOf(precisionTest.getTotalCost()).split("\\.")[1].length()); // Should have 2 decimal
                                                                                                // places
    }

    @Test
    void testTimestampBehavior() {
        // Test that timestamp is set on creation
        assertNotNull(receipt.getTimestamp());
        assertTrue(receipt.getTimestamp().isBefore(LocalDateTime.now()) ||
                receipt.getTimestamp().isEqual(LocalDateTime.now()));

        // Store initial timestamp
        LocalDateTime initialTimestamp = receipt.getTimestamp();

        // Try to modify receipt and verify timestamp hasn't changed
        receipt.setTotalCost(200.0);
        assertEquals(initialTimestamp, receipt.getTimestamp());
    }

    @Test
    void testCreditCardValidation() {
        // Test invalid credit card number
        CreditCard invalidCard = new CreditCard();
        invalidCard.setCardNum("1234");
        invalidCard.setCardName("Test User");
        invalidCard.setExpDate(YearMonth.of(2025, 12));
        invalidCard.setSecurityCode("123");

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, invalidCard, address, 5);
        });

        // Test expired credit card
        CreditCard expiredCard = new CreditCard();
        expiredCard.setCardNum("4111111111111111");
        expiredCard.setCardName("Test User");
        expiredCard.setExpDate(YearMonth.of(2020, 12));
        expiredCard.setSecurityCode("123");

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, expiredCard, address, 5);
        });

        // Test null credit card
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, null, address, 5);
        });
    }

    @Test
    void testAddressValidation() {
        // Test invalid address (missing required fields)
        Address invalidAddress = new Address();
        invalidAddress.setStreetName("Test Street");
        // Missing other required fields

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, invalidAddress, 5);
        });

        // Test null address
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, null, 5);
        });

        // Test address format validation
        Address invalidPostalCode = new Address();
        invalidPostalCode.setStreetName("Test Street");
        invalidPostalCode.setStreetNum(123);
        invalidPostalCode.setPostalCode("INVALID");
        invalidPostalCode.setCity("Test City");
        invalidPostalCode.setProvince("Test Province");
        invalidPostalCode.setCountry("Test Country");

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, invalidPostalCode, 5);
        });
    }

    @Test
    void testNullValidations() {
        // Test null user
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), null, 100.0, creditCard, address, 5);
        });

        // Test null itemID
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(null, user, 100.0, creditCard, address, 5);
        });

        // Test invalid total cost
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, -100.0, creditCard, address, 5);
        });

        // Test invalid shipping time
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, -1);
        });
    }

    @Test
    void testUserRelationship() {
        // Test bidirectional relationship
        assertTrue(user.getReceipts().contains(receipt));
        assertEquals(user, receipt.getUser());

        // Test removing receipt from user
        user.removeReceipt(receipt);
        assertFalse(user.getReceipts().contains(receipt));
        assertNull(receipt.getUser());
    }
}