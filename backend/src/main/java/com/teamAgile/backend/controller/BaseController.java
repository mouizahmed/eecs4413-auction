package com.teamAgile.backend.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getCurrentUser(HttpServletRequest request) {
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

		return (Map<String, Object>) session.getAttribute("user");
	}
}