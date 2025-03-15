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
    void testSignUpDTOConstructor() {
        // Create SignUpDTO
        SignUpDTO signUpDTO = new SignUpDTO();
        try {
            java.lang.reflect.Field usernameField = SignUpDTO.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(signUpDTO, username);

            java.lang.reflect.Field passwordField = SignUpDTO.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(signUpDTO, password);

            java.lang.reflect.Field firstNameField = SignUpDTO.class.getDeclaredField("firstName");
            firstNameField.setAccessible(true);
            firstNameField.set(signUpDTO, firstName);

            java.lang.reflect.Field lastNameField = SignUpDTO.class.getDeclaredField("lastName");
            lastNameField.setAccessible(true);
            lastNameField.set(signUpDTO, lastName);

            java.lang.reflect.Field streetNameField = SignUpDTO.class.getDeclaredField("streetName");
            streetNameField.setAccessible(true);
            streetNameField.set(signUpDTO, streetName);

            java.lang.reflect.Field streetNumField = SignUpDTO.class.getDeclaredField("streetNum");
            streetNumField.setAccessible(true);
            streetNumField.set(signUpDTO, streetNum);

            java.lang.reflect.Field postalCodeField = SignUpDTO.class.getDeclaredField("postalCode");
            postalCodeField.setAccessible(true);
            postalCodeField.set(signUpDTO, postalCode);

            java.lang.reflect.Field cityField = SignUpDTO.class.getDeclaredField("city");
            cityField.setAccessible(true);
            cityField.set(signUpDTO, city);

            java.lang.reflect.Field countryField = SignUpDTO.class.getDeclaredField("country");
            countryField.setAccessible(true);
            countryField.set(signUpDTO, country);

            java.lang.reflect.Field securityQuestionField = SignUpDTO.class.getDeclaredField("securityQuestion");
            securityQuestionField.setAccessible(true);
            securityQuestionField.set(signUpDTO, securityQuestion);

            java.lang.reflect.Field securityAnswerField = SignUpDTO.class.getDeclaredField("securityAnswer");
            securityAnswerField.setAccessible(true);
            securityAnswerField.set(signUpDTO, securityAnswer);
        } catch (Exception e) {
            fail("Failed to set up SignUpDTO: " + e.getMessage());
        }

        // Create user from SignUpDTO
        User newUser = new User(signUpDTO);

        assertNull(newUser.getUserID()); // ID should be null as it's generated by the database
        assertEquals(firstName, newUser.getFirstName());
        assertEquals(lastName, newUser.getLastName());
        assertEquals(username, newUser.getUsername());
        assertTrue(BCryptHashing.checkPassword(password, newUser.getPassword()));
        assertEquals(securityQuestion, newUser.getSecurityQuestion());
        assertEquals(securityAnswer, newUser.getSecurityAnswer());

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