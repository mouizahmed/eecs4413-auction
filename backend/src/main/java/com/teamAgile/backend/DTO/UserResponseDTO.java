package com.teamAgile.backend.DTO;

import java.util.UUID;

import com.teamAgile.backend.model.User;

public class UserResponseDTO {
    private UUID userID;
    private String username;
    private String firstName;
    private String lastName;
    private Integer streetNum;
    private String streetName;
    private String postalCode;
    private String city;
    private String province;
    private String country;

    public UserResponseDTO() {
    }

    public UserResponseDTO(User user) {
        this.userID = user.getUserID();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        if (user.getAddress() != null) {
            this.streetNum = user.getAddress().getStreetNum();
            this.streetName = user.getAddress().getStreetName();
            this.postalCode = user.getAddress().getPostalCode();
            this.city = user.getAddress().getCity();
            this.province = user.getAddress().getProvince();
            this.country = user.getAddress().getCountry();
        }
    }

    // Getters and setters
    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(Integer streetNum) {
        this.streetNum = streetNum;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getProvince() {
    	return province;
    }
    
    public void setProvince(String province) {
    	this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}