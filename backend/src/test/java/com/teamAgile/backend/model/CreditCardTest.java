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
        CreditCard validCard = new CreditCard("4111111111111111", "Jane Smith", YearMonth.of(2025, 12), "456");
        assertNotNull(validCard);
        assertEquals("4111111111111111", validCard.getCardNum());
        assertEquals("Jane Smith", validCard.getCardName());
        assertEquals(YearMonth.of(2025, 12), validCard.getExpDate());
        assertEquals("456", validCard.getSecurityCode());

        CreditCard emptyCard = new CreditCard();
        assertNotNull(emptyCard);
        assertNull(emptyCard.getCardNum());
        assertNull(emptyCard.getCardName());
        assertNull(emptyCard.getExpDate());
        assertNull(emptyCard.getSecurityCode());
    }

    @Test
    void testGettersAndSetters() {
        String newCardNum = "9876543210987654";
        creditCard.setCardNum(newCardNum);
        assertEquals(newCardNum, creditCard.getCardNum());

        creditCard.setCardNum(null);
        assertNull(creditCard.getCardNum());

        creditCard.setCardNum("");
        assertEquals("", creditCard.getCardNum());

        String newCardName = "Jane Smith";
        creditCard.setCardName(newCardName);
        assertEquals(newCardName, creditCard.getCardName());

        creditCard.setCardName(null);
        assertNull(creditCard.getCardName());

        creditCard.setCardName("");
        assertEquals("", creditCard.getCardName());

        YearMonth newExpDate = YearMonth.now().plusYears(2);
        creditCard.setExpDate(newExpDate);
        assertEquals(newExpDate, creditCard.getExpDate());

        creditCard.setExpDate(null);
        assertNull(creditCard.getExpDate());

        YearMonth pastExpDate = YearMonth.now().minusYears(1);
        creditCard.setExpDate(pastExpDate);
        assertEquals(pastExpDate, creditCard.getExpDate());

        String newSecurityCode = "456";
        creditCard.setSecurityCode(newSecurityCode);
        assertEquals(newSecurityCode, creditCard.getSecurityCode());

        creditCard.setSecurityCode(null);
        assertNull(creditCard.getSecurityCode());

        creditCard.setSecurityCode("");
        assertEquals("", creditCard.getSecurityCode());
    }

    @Test
    void testCreditCardValidation() {
        creditCard.setCardNum("4111111111111111");
        assertEquals("4111111111111111", creditCard.getCardNum());

        creditCard.setCardNum("341111111111111");
        assertEquals("341111111111111", creditCard.getCardNum());

        creditCard.setCardNum("30111111111111");
        assertEquals("30111111111111", creditCard.getCardNum());

        creditCard.setCardNum("4111 1111 1111 1111");
        assertEquals("4111 1111 1111 1111", creditCard.getCardNum());

        creditCard.setSecurityCode("123");
        assertEquals("123", creditCard.getSecurityCode());

        creditCard.setSecurityCode("1234");
        assertEquals("1234", creditCard.getSecurityCode());

        creditCard.setSecurityCode("12");
        assertEquals("12", creditCard.getSecurityCode());

        creditCard.setSecurityCode("12345");
        assertEquals("12345", creditCard.getSecurityCode());
    }

    @Test
    void testEdgeCases() {
        String longText = "a".repeat(255);

        creditCard.setCardNum(longText);
        assertEquals(longText, creditCard.getCardNum());

        creditCard.setCardName(longText);
        assertEquals(longText, creditCard.getCardName());

        creditCard.setSecurityCode(longText);
        assertEquals(longText, creditCard.getSecurityCode());

        YearMonth farFuture = YearMonth.of(2100, 12);
        creditCard.setExpDate(farFuture);
        assertEquals(farFuture, creditCard.getExpDate());
    }
}