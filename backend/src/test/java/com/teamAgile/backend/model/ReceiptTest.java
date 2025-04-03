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
    private static final double DELTA = 0.001;

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
        Receipt fastShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 1);
        Receipt normalShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 5);
        Receipt slowShipping = new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, 10);

        assertEquals(100.0, fastShipping.getTotalCost(), DELTA);
        assertEquals(100.0, normalShipping.getTotalCost(), DELTA);
        assertEquals(100.0, slowShipping.getTotalCost(), DELTA);

        Receipt precisionTest = new Receipt(UUID.randomUUID(), user, 10.99, creditCard, address, 5);
        assertEquals(10.99, precisionTest.getTotalCost(), DELTA);
        assertEquals(2, String.valueOf(precisionTest.getTotalCost()).split("\\.")[1].length());
    }

    @Test
    void testTimestampBehavior() {
        assertNotNull(receipt.getTimestamp());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneSecondBefore = now.minusSeconds(1);
        LocalDateTime oneSecondAfter = now.plusSeconds(1);

        LocalDateTime receiptTime = receipt.getTimestamp();
        assertTrue(
                (receiptTime.isEqual(oneSecondBefore) || receiptTime.isAfter(oneSecondBefore)) &&
                        (receiptTime.isEqual(oneSecondAfter) || receiptTime.isBefore(oneSecondAfter)),
                "Receipt timestamp should be within 1 second of current time");

        LocalDateTime initialTimestamp = receipt.getTimestamp();

        receipt.setTotalCost(200.0);
        assertEquals(initialTimestamp, receipt.getTimestamp());
    }

    @Test
    void testCreditCardValidation() {
        CreditCard invalidCard = new CreditCard();
        invalidCard.setCardNum("1234");
        invalidCard.setCardName("Test User");
        invalidCard.setExpDate(YearMonth.of(2025, 12));
        invalidCard.setSecurityCode("123");

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, invalidCard, address, 5);
        });

        CreditCard expiredCard = new CreditCard();
        expiredCard.setCardNum("4111111111111111");
        expiredCard.setCardName("Test User");
        expiredCard.setExpDate(YearMonth.of(2020, 12));
        expiredCard.setSecurityCode("123");

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, expiredCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, null, address, 5);
        });
    }

    @Test
    void testAddressValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, null, 5);
        });

        Address incompleteAddress = new Address();
        incompleteAddress.setStreetName("Test Street");
        Receipt receiptWithIncompleteAddress = new Receipt(UUID.randomUUID(), user, 100.0, creditCard,
                incompleteAddress, 5);
        assertNotNull(receiptWithIncompleteAddress);

        Address addressWithInvalidPostal = new Address();
        addressWithInvalidPostal.setStreetName("Test Street");
        addressWithInvalidPostal.setStreetNum(123);
        addressWithInvalidPostal.setPostalCode("INVALID");
        addressWithInvalidPostal.setCity("Test City");
        addressWithInvalidPostal.setProvince("Test Province");
        addressWithInvalidPostal.setCountry("Test Country");

        Receipt receiptWithInvalidPostal = new Receipt(UUID.randomUUID(), user, 100.0, creditCard,
                addressWithInvalidPostal, 5);
        assertNotNull(receiptWithInvalidPostal);
    }

    @Test
    void testNullValidations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), null, 100.0, creditCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(null, user, 100.0, creditCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, -100.0, creditCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 100.0, creditCard, address, -1);
        });
    }

    @Test
    void testPaymentAmountValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            receipt.setTotalCost(-100.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            receipt.setTotalCost(0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            receipt.setTotalCost(null);
        });

        Double validAmount = 200.0;
        receipt.setTotalCost(validAmount);
        assertEquals(validAmount, receipt.getTotalCost(), DELTA);

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, -100.0, creditCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, 0.0, creditCard, address, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(UUID.randomUUID(), user, null, creditCard, address, 5);
        });
    }

    @Test
    void testUserRelationship() {
        assertTrue(user.getReceipts().contains(receipt));
        assertEquals(user, receipt.getUser());

        user.removeReceipt(receipt);
        assertFalse(user.getReceipts().contains(receipt));
        assertNull(receipt.getUser());
    }
}