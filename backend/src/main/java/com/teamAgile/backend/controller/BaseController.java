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

		Object userID = user.get("userID");
		if (userID == null) {
			return null;
		}

		User userObj = new User((UUID) user.get("userID"), (String) user.get("firstName"),
				(String) user.get("lastName"), (String) user.get("username"), (String) user.get("streetName"),
				(Integer) user.get("streetNum"), (String) user.get("postalCode"), (String) user.get("city"),
				(String) user.get("country"));

		return userObj;
	}
}