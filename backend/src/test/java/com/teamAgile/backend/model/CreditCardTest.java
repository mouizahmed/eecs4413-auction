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
    void testGettersAndSetters() {
        // Test cardNum
        String newCardNum = "9876543210987654";
        creditCard.setCardNum(newCardNum);
        assertEquals(newCardNum, creditCard.getCardNum());

        // Test cardName
        String newCardName = "Jane Smith";
        creditCard.setCardName(newCardName);
        assertEquals(newCardName, creditCard.getCardName());

        // Test expDate
        YearMonth newExpDate = YearMonth.now().plusYears(2);
        creditCard.setExpDate(newExpDate);
        assertEquals(newExpDate, creditCard.getExpDate());

        // Test securityCode
        String newSecurityCode = "456";
        creditCard.setSecurityCode(newSecurityCode);
        assertEquals(newSecurityCode, creditCard.getSecurityCode());
    }
}