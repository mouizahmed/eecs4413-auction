package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.YearMonth;

class CreditCardTest {
    private CreditCard creditCard;
    private String testCardNum;
    private String testCardName;
    private YearMonth testExpDate;
    private String testSecurityCode;

    @BeforeEach
    void setUp() {
        testCardNum = "1234567890123456";
        testCardName = "John Doe";
        testExpDate = YearMonth.now().plusYears(1);
        testSecurityCode = "123";
        creditCard = new CreditCard(testCardNum, testCardName, testExpDate, testSecurityCode);
    }

    @Test
    void testConstructor() {
        // Test valid constructor
        CreditCard validCard = new CreditCard("4111111111111111", "Jane Smith", YearMonth.of(2025, 12), "456");
        assertNotNull(validCard);
        assertEquals("4111111111111111", validCard.getCardNum());
        assertEquals("Jane Smith", validCard.getCardName());
        assertEquals(YearMonth.of(2025, 12), validCard.getExpDate());
        assertEquals("456", validCard.getSecurityCode());

        // Test empty constructor
        CreditCard emptyCard = new CreditCard();
        assertNotNull(emptyCard);
        assertNull(emptyCard.getCardNum());
        assertNull(emptyCard.getCardName());
        assertNull(emptyCard.getExpDate());
        assertNull(emptyCard.getSecurityCode());
    }

    @Test
    void testGettersAndSetters() {
        // Test cardNum
        String newCardNum = "9876543210987654";
        creditCard.setCardNum(newCardNum);
        assertEquals(newCardNum, creditCard.getCardNum());

        // Test null cardNum
        creditCard.setCardNum(null);
        assertNull(creditCard.getCardNum());

        // Test empty cardNum
        creditCard.setCardNum("");
        assertEquals("", creditCard.getCardNum());

        // Test cardName
        String newCardName = "Jane Smith";
        creditCard.setCardName(newCardName);
        assertEquals(newCardName, creditCard.getCardName());

        // Test null cardName
        creditCard.setCardName(null);
        assertNull(creditCard.getCardName());

        // Test empty cardName
        creditCard.setCardName("");
        assertEquals("", creditCard.getCardName());

        // Test expDate
        YearMonth newExpDate = YearMonth.now().plusYears(2);
        creditCard.setExpDate(newExpDate);
        assertEquals(newExpDate, creditCard.getExpDate());

        // Test null expDate
        creditCard.setExpDate(null);
        assertNull(creditCard.getExpDate());

        // Test past expDate (while this might be invalid in practice, the model doesn't
        // validate it)
        YearMonth pastExpDate = YearMonth.now().minusYears(1);
        creditCard.setExpDate(pastExpDate);
        assertEquals(pastExpDate, creditCard.getExpDate());

        // Test securityCode
        String newSecurityCode = "456";
        creditCard.setSecurityCode(newSecurityCode);
        assertEquals(newSecurityCode, creditCard.getSecurityCode());

        // Test null securityCode
        creditCard.setSecurityCode(null);
        assertNull(creditCard.getSecurityCode());

        // Test empty securityCode
        creditCard.setSecurityCode("");
        assertEquals("", creditCard.getSecurityCode());
    }

    @Test
    void testCreditCardValidation() {
        // Test various credit card formats

        // Valid format - 16 digits
        creditCard.setCardNum("4111111111111111");
        assertEquals("4111111111111111", creditCard.getCardNum());

        // Valid format - 15 digits (for Amex)
        creditCard.setCardNum("341111111111111");
        assertEquals("341111111111111", creditCard.getCardNum());

        // Valid format - 14 digits (for some cards)
        creditCard.setCardNum("30111111111111");
        assertEquals("30111111111111", creditCard.getCardNum());

        // Valid format - with spaces (model doesn't validate or strip spaces)
        creditCard.setCardNum("4111 1111 1111 1111");
        assertEquals("4111 1111 1111 1111", creditCard.getCardNum());

        // Test various security code formats

        // Valid format - 3 digits (Visa/MC)
        creditCard.setSecurityCode("123");
        assertEquals("123", creditCard.getSecurityCode());

        // Valid format - 4 digits (Amex)
        creditCard.setSecurityCode("1234");
        assertEquals("1234", creditCard.getSecurityCode());

        // As model doesn't validate, these also pass:
        creditCard.setSecurityCode("12");
        assertEquals("12", creditCard.getSecurityCode());

        creditCard.setSecurityCode("12345");
        assertEquals("12345", creditCard.getSecurityCode());
    }

    @Test
    void testEdgeCases() {
        // Test with very long strings (while these might be invalid in practice, the
        // model doesn't validate)
        String longText = "a".repeat(255); // Max typical VARCHAR length

        creditCard.setCardNum(longText);
        assertEquals(longText, creditCard.getCardNum());

        creditCard.setCardName(longText);
        assertEquals(longText, creditCard.getCardName());

        creditCard.setSecurityCode(longText);
        assertEquals(longText, creditCard.getSecurityCode());

        // Test far future date
        YearMonth farFuture = YearMonth.of(2100, 12);
        creditCard.setExpDate(farFuture);
        assertEquals(farFuture, creditCard.getExpDate());
    }
}