package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTest {

    private Address address;
    private String streetName;
    private Integer streetNum;
    private String postalCode;
    private String city;
    private String country;

    @BeforeEach
    void setUp() {
        streetName = "Main St";
        streetNum = 123;
        postalCode = "12345";
        city = "New York";
        country = "USA";

        address = new Address(streetName, streetNum, postalCode, city, country);
    }

    @Test
    void testDefaultConstructor() {
        Address newAddress = new Address();
        assertNotNull(newAddress);
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(streetName, address.getStreetName());
        assertEquals(streetNum, address.getStreetNum());
        assertEquals(postalCode, address.getPostalCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());
    }

    @Test
    void testGettersAndSetters() {
        // Test initial values
        assertEquals(streetName, address.getStreetName());
        assertEquals(streetNum, address.getStreetNum());
        assertEquals(postalCode, address.getPostalCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());

        // Test setters with new values
        String newStreetName = "Broadway";
        Integer newStreetNum = 456;
        String newPostalCode = "67890";
        String newCity = "Los Angeles";
        String newCountry = "USA";

        address.setStreetName(newStreetName);
        address.setStreetNum(newStreetNum);
        address.setPostalCode(newPostalCode);
        address.setCity(newCity);
        address.setCountry(newCountry);

        assertEquals(newStreetName, address.getStreetName());
        assertEquals(newStreetNum, address.getStreetNum());
        assertEquals(newPostalCode, address.getPostalCode());
        assertEquals(newCity, address.getCity());
        assertEquals(newCountry, address.getCountry());
    }
}