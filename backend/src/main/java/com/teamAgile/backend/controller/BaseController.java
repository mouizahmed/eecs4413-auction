package com.teamAgile.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.UserService;

@Controller
public class BaseController {

	@Autowired
	private UserService userService;

	protected User getCurrentUser(HttpServletRequest request) {
		// Get the authenticated username from SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated() &&
				!authentication.getPrincipal().toString().equals("anonymousUser")) {
			String username = authentication.getName();
			return userService.findByUsername(username);
		}

		return null;
	}
}