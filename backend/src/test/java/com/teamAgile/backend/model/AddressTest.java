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
    void testGettersAndSetters() {
        // Test street name
        address.setStreetName("New Street");
        assertEquals("New Street", address.getStreetName());

        // Test street number
        address.setStreetNum(456);
        assertEquals(456, address.getStreetNum());

        // Test postal code
        address.setPostalCode("X1Y2Z3");
        assertEquals("X1Y2Z3", address.getPostalCode());

        // Test city
        address.setCity("Vancouver");
        assertEquals("Vancouver", address.getCity());

        // Test province
        address.setProvince("British Columbia");
        assertEquals("British Columbia", address.getProvince());

        // Test country
        address.setCountry("Canada");
        assertEquals("Canada", address.getCountry());
    }
}