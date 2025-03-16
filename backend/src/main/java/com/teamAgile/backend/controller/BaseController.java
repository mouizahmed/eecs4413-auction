package com.teamAgile.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import com.teamAgile.backend.DTO.UserResponseDTO;
import com.teamAgile.backend.model.User;

@Controller
public class BaseController {
	protected User getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		UserResponseDTO userDTO = (UserResponseDTO) session.getAttribute("user");
		if (userDTO == null) {
			return null;
		}

		User user = new User();
		user.setUserID(userDTO.getUserID());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setUsername(userDTO.getUsername());

		com.teamAgile.backend.model.Address address = new com.teamAgile.backend.model.Address(userDTO.getStreetName(),
				userDTO.getStreetNum(), userDTO.getPostalCode(), userDTO.getCity(), userDTO.getCountry());
		user.setAddress(address);

		return user;
	}
}