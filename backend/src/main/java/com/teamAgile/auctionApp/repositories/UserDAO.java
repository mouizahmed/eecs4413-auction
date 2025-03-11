package com.teamAgile.auctionApp.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.teamAgile.auctionApp.models.User;

public class UserDAO {
	private static HashMap<String, User> users = new HashMap<String, User>();
	
	public User create(User user) {
		if (users.get(user.getUsername()) != null) return null;
		
		users.put(user.getUsername(), user);
		return user;
	}
	
	public HashMap<String, User> readAll() {
		return users;
	}
	
	public User read(String username) {
		return users.get(username);
	}
	
	public User signIn(User userRequest) {
		User user = users.get(userRequest.getUsername());
		
		if (user == null) return null;
		
		if (user.getPassword().equals(userRequest.getPassword()) == false) return null;
		
		return user;
	}
}
