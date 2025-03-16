package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreditCardTest {

    private CreditCard creditCard;
    private String cardNum;
    private String cardName;
    private YearMonth expDate;
    private String securityCode;

    @BeforeEach
    void setUp() {
        cardNum = "1234567890123456";
        cardName = "Test User";
        expDate = YearMonth.now().plusYears(1);
        securityCode = "123";

        creditCard = new CreditCard();
        creditCard.setCardNum(cardNum);
        creditCard.setCardName(cardName);
        creditCard.setExpDate(expDate);
        creditCard.setSecurityCode(securityCode);
    }

    @Test
    void testDefaultConstructor() {
        CreditCard newCreditCard = new CreditCard();
        assertNotNull(newCreditCard);
    }

    @Test
    void testParameterizedConstructor() {
        CreditCard newCreditCard = new CreditCard(cardNum, cardName, expDate, securityCode);

        assertEquals(cardNum, newCreditCard.getCardNum());
        assertEquals(cardName, newCreditCard.getCardName());
        assertEquals(expDate, newCreditCard.getExpDate());
        assertEquals(securityCode, newCreditCard.getSecurityCode());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(cardNum, creditCard.getCardNum());
        assertEquals(cardName, creditCard.getCardName());
        assertEquals(expDate, creditCard.getExpDate());
        assertEquals(securityCode, creditCard.getSecurityCode());
        
        String newCardNum = "9876543210987654";
        String newCardName = "New User";
        YearMonth newExpDate = YearMonth.now().plusYears(2);
        String newSecurityCode = "456";

        creditCard.setCardNum(newCardNum);
        creditCard.setCardName(newCardName);
        creditCard.setExpDate(newExpDate);
        creditCard.setSecurityCode(newSecurityCode);

        assertEquals(newCardNum, creditCard.getCardNum());
        assertEquals(newCardName, creditCard.getCardName());
        assertEquals(newExpDate, creditCard.getExpDate());
        assertEquals(newSecurityCode, creditCard.getSecurityCode());
    }
}