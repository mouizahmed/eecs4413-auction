package com.teamAgile.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.util.BCryptHashing;

class UserTest {

    private User user;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String securityQuestion;
    private String securityAnswer;
    private String streetName;
    private Integer streetNum;
    private String postalCode;
    private String city;
    private String country;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        firstName = "John";
        lastName = "Doe";
        username = "johndoe";
        password = "password123";
        securityQuestion = "What is your pet's name?";
        securityAnswer = "Fluffy";
        streetName = "Main St";
        streetNum = 123;
        postalCode = "12345";
        city = "New York";
        country = "USA";

        user = new User();
        user.setUserID(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);

        Address address = new Address(streetName, streetNum, postalCode, city, country);
        user.setAddress(address);
    }

    @Test
    void testDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
    }

    @Test
    void testParameterizedConstructor() {
        User newUser = new User(userId, firstName, lastName, username, streetName, streetNum, postalCode, city,
                country);

        assertEquals(userId, newUser.getUserID());
        assertEquals(firstName, newUser.getFirstName());
        assertEquals(lastName, newUser.getLastName());
        assertEquals(username, newUser.getUsername());
        assertNull(newUser.getPassword());
        assertNull(newUser.getSecurityQuestion());
        assertNull(newUser.getSecurityAnswer());

        Address address = newUser.getAddress();
        assertNotNull(address);
        assertEquals(streetName, address.getStreetName());
        assertEquals(streetNum, address.getStreetNum());
        assertEquals(postalCode, address.getPostalCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(userId, user.getUserID());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(username, user.getUsername());
        assertTrue(BCryptHashing.checkPassword(password, user.getPassword()));
        assertEquals(securityQuestion, user.getSecurityQuestion());
        assertEquals(securityAnswer, user.getSecurityAnswer());

        Address address = user.getAddress();
        assertNotNull(address);
        assertEquals(streetName, address.getStreetName());
        assertEquals(streetNum, address.getStreetNum());
        assertEquals(postalCode, address.getPostalCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());

        // Test setters with new values
        UUID newUserId = UUID.randomUUID();
        String newFirstName = "Jane";
        String newLastName = "Smith";
        String newUsername = "janesmith";
        String newPassword = "newpassword456";
        String newSecurityQuestion = "What is your mother's maiden name?";
        String newSecurityAnswer = "Johnson";
        String newStreetName = "Broadway";
        Integer newStreetNum = 456;
        String newPostalCode = "67890";
        String newCity = "Los Angeles";
        String newCountry = "USA";

        user.setUserID(newUserId);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setSecurityQuestion(newSecurityQuestion);
        user.setSecurityAnswer(newSecurityAnswer);

        Address newAddress = new Address(newStreetName, newStreetNum, newPostalCode, newCity, newCountry);
        user.setAddress(newAddress);

        assertEquals(newUserId, user.getUserID());
        assertEquals(newFirstName, user.getFirstName());
        assertEquals(newLastName, user.getLastName());
        assertEquals(newUsername, user.getUsername());
        assertTrue(BCryptHashing.checkPassword(newPassword, user.getPassword()));
        assertEquals(newSecurityQuestion, user.getSecurityQuestion());
        assertEquals(newSecurityAnswer, user.getSecurityAnswer());

        Address updatedAddress = user.getAddress();
        assertNotNull(updatedAddress);
        assertEquals(newStreetName, updatedAddress.getStreetName());
        assertEquals(newStreetNum, updatedAddress.getStreetNum());
        assertEquals(newPostalCode, updatedAddress.getPostalCode());
        assertEquals(newCity, updatedAddress.getCity());
        assertEquals(newCountry, updatedAddress.getCountry());
    }
}