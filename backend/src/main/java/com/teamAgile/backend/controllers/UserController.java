package com.teamAgile.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.models.User;
import com.teamAgile.backend.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public UserController(UserService userService, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	@GetMapping("/get-all")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@RequestBody User user) {
		User createdUser = userService.signUp(user);
		if (createdUser == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PostMapping("/sign-in")
	public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
		if (loginRequest.get("username") == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a username");
		if (loginRequest.get("password") == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a password");

		try {
			// Authenticate using Spring Security
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.get("username"), loginRequest.get("password")));

			// Set the authentication in the SecurityContext
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Get the authenticated user
			User user = userService.signIn(loginRequest.get("username"), loginRequest.get("password"));

			// Create new session
			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			Object userObject = Map.of("userID", user.getUserID(), "username", user.getUsername(), "streetNumber", user.getStreetNumber(), "streetName", user.getStreetName(), "postalCode", user.getPostalCode(), "country", user.getCountry());
			session.setAttribute("user", userObject);
			session.setMaxInactiveInterval(30 * 60); // 30 minutes

			return ResponseEntity.ok().body(userObject);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
		}
	}

	@PostMapping("/sign-out")
	public ResponseEntity<?> signOut(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			SecurityContextHolder.clearContext();
			return ResponseEntity.ok("Successfully signed out");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No active session found");
	}

	// sent password request request

	// confirm password request code
}
