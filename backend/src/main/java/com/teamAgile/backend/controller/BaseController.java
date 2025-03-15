package com.teamAgile.backend.controller;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.teamAgile.backend.model.User;

@Controller
public class BaseController {
	@SuppressWarnings("unchecked")
	protected User getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");

		if (user == null) {
			return null;
		}
		
		User userObj = new User();
		userObj.setUsername((String) user.get("username"));
		userObj.setPostalCode((String) user.get("postalCode"));
		userObj.setStreetName((String) user.get("streetName"));
		userObj.setStreetNumber((int) user.get("streetNumber"));
		userObj.setCountry((String) user.get("country"));
		userObj.setFirstName((String) user.get("firstName"));
		userObj.setLastName((String) user.get("lastName"));
		userObj.setUserID((UUID) user.get("userID"));
		
		

//		Object userID = user.get("userID");
//		if (userID == null) {
//			return null;
//		}

		return userObj;
	}
}