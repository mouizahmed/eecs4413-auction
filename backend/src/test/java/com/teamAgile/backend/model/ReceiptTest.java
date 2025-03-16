package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiptTest {

    private Receipt receipt;
    private UUID itemId;
    private User user;
    private Double totalCost;
    private CreditCard creditCard;
    private Address address;
    private Integer shippingTime;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();

        user = new User();
        user.setUserID(UUID.randomUUID());
        user.setUsername("testUser");

        totalCost = 100.0;

        creditCard = new CreditCard();
        creditCard.setCardNum("1234567890123456");
        creditCard.setCardName("Test User");
        creditCard.setExpDate(YearMonth.now().plusYears(1));
        creditCard.setSecurityCode("123");

        address = new Address();
        address.setStreetName("Main St");
        address.setStreetNum(123);
        address.setPostalCode("12345");
        address.setCity("Test City");
        address.setCountry("Test Country");

        shippingTime = 5;

        receipt = new Receipt();
        receipt.setItemID(itemId);
        receipt.setUser(user);
        receipt.setTotalCost(totalCost);
        receipt.setCreditCard(creditCard);
        receipt.setAddress(address);
        receipt.setShippingTime(shippingTime);
    }

    @Test
    void testDefaultConstructor() {
        Receipt newReceipt = new Receipt();
        assertNotNull(newReceipt);
    }

    @Test
    void testParameterizedConstructor() {
        Receipt newReceipt = new Receipt(itemId, user, totalCost, creditCard, address, shippingTime);

        assertEquals(itemId, newReceipt.getItemID());
        assertEquals(user, newReceipt.getUser());
        assertEquals(totalCost, newReceipt.getTotalCost());
        assertEquals(creditCard, newReceipt.getCreditCard());
        assertEquals(address, newReceipt.getAddress());
        assertEquals(shippingTime, newReceipt.getShippingTime());
        assertNull(newReceipt.getTimestamp());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(itemId, receipt.getItemID());
        assertEquals(user, receipt.getUser());
        assertEquals(totalCost, receipt.getTotalCost());
        assertEquals(creditCard, receipt.getCreditCard());
        assertEquals(address, receipt.getAddress());
        assertEquals(shippingTime, receipt.getShippingTime());

    
        UUID newItemId = UUID.randomUUID();
        User newUser = new User();
        newUser.setUserID(UUID.randomUUID());
        newUser.setUsername("newUser");
        Double newTotalCost = 200.0;

        CreditCard newCreditCard = new CreditCard();
        newCreditCard.setCardNum("9876543210987654");
        newCreditCard.setCardName("New User");
        newCreditCard.setExpDate(YearMonth.now().plusYears(2));
        newCreditCard.setSecurityCode("456");

        Address newAddress = new Address();
        newAddress.setStreetName("Second St");
        newAddress.setStreetNum(456);
        newAddress.setPostalCode("67890");
        newAddress.setCity("New City");
        newAddress.setCountry("New Country");

        Integer newShippingTime = 10;

        receipt.setItemID(newItemId);
        receipt.setUser(newUser);
        receipt.setTotalCost(newTotalCost);
        receipt.setCreditCard(newCreditCard);
        receipt.setAddress(newAddress);
        receipt.setShippingTime(newShippingTime);

        assertEquals(newItemId, receipt.getItemID());
        assertEquals(newUser, receipt.getUser());
        assertEquals(newTotalCost, receipt.getTotalCost());
        assertEquals(newCreditCard, receipt.getCreditCard());
        assertEquals(newAddress, receipt.getAddress());
        assertEquals(newShippingTime, receipt.getShippingTime());
    }

    @Test
    void testReceiptID() {

        UUID receiptId = UUID.randomUUID();
        receipt.setReceiptID(receiptId);
        assertEquals(receiptId, receipt.getReceiptID());
    }

    @Test
    void testTimestamp() {
        assertNull(receipt.getTimestamp());
    }
}