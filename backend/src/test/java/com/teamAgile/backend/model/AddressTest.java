package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTest {
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("Main Street", 123, "A1B2C3", "Toronto", "Ontario", "Canada");
    }

    @Test
    void testConstructor() {
        Address validAddress = new Address("Elm Street", 456, "X9Y8Z7", "Vancouver", "BC", "Canada");
        assertNotNull(validAddress);
        assertEquals("Elm Street", validAddress.getStreetName());
        assertEquals(456, validAddress.getStreetNum());
        assertEquals("X9Y8Z7", validAddress.getPostalCode());
        assertEquals("Vancouver", validAddress.getCity());
        assertEquals("BC", validAddress.getProvince());
        assertEquals("Canada", validAddress.getCountry());

        Address emptyAddress = new Address();
        assertNotNull(emptyAddress);
        assertNull(emptyAddress.getStreetName());
        assertNull(emptyAddress.getStreetNum());
        assertNull(emptyAddress.getPostalCode());
        assertNull(emptyAddress.getCity());
        assertNull(emptyAddress.getProvince());
        assertNull(emptyAddress.getCountry());
    }

    @Test
    void testGettersAndSetters() {
        address.setStreetName("New Street");
        assertEquals("New Street", address.getStreetName());
        address.setStreetName(null);
        assertNull(address.getStreetName());

        address.setStreetNum(456);
        assertEquals(456, address.getStreetNum());
        address.setStreetNum(null);
        assertNull(address.getStreetNum());
        address.setStreetNum(0);
        assertEquals(0, address.getStreetNum());
        address.setStreetNum(-1);
        assertEquals(-1, address.getStreetNum());

        address.setPostalCode("X1Y2Z3");
        assertEquals("X1Y2Z3", address.getPostalCode());
        address.setPostalCode(null);
        assertNull(address.getPostalCode());
        address.setPostalCode("");
        assertEquals("", address.getPostalCode());

        address.setCity("Vancouver");
        assertEquals("Vancouver", address.getCity());
        address.setCity(null);
        assertNull(address.getCity());
        address.setCity("");
        assertEquals("", address.getCity());

        address.setProvince("British Columbia");
        assertEquals("British Columbia", address.getProvince());
        address.setProvince(null);
        assertNull(address.getProvince());
        address.setProvince("");
        assertEquals("", address.getProvince());

        address.setCountry("USA");
        assertEquals("USA", address.getCountry());
        address.setCountry(null);
        assertNull(address.getCountry());
        address.setCountry("");
        assertEquals("", address.getCountry());
    }

    @Test
    void testAddressCompleteValidation() {
        Address complete = new Address("Maple Ave", 789, "M5V2L7", "Montreal", "Quebec", "Canada");

        assertNotNull(complete.getStreetName());
        assertNotNull(complete.getStreetNum());
        assertNotNull(complete.getPostalCode());
        assertNotNull(complete.getCity());
        assertNotNull(complete.getProvince());
        assertNotNull(complete.getCountry());

        assertEquals("Maple Ave", complete.getStreetName());
        assertEquals(789, complete.getStreetNum());
        assertEquals("M5V2L7", complete.getPostalCode());
        assertEquals("Montreal", complete.getCity());
        assertEquals("Quebec", complete.getProvince());
        assertEquals("Canada", complete.getCountry());
    }

    @Test
    void testEdgeCases() {
        String longText = "a".repeat(255);

        address.setStreetName(longText);
        assertEquals(longText, address.getStreetName());

        address.setPostalCode(longText);
        assertEquals(longText, address.getPostalCode());

        address.setCity(longText);
        assertEquals(longText, address.getCity());

        address.setProvince(longText);
        assertEquals(longText, address.getProvince());

        address.setCountry(longText);
        assertEquals(longText, address.getCountry());

        address.setStreetNum(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, address.getStreetNum());

        address.setStreetNum(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, address.getStreetNum());
    }
}