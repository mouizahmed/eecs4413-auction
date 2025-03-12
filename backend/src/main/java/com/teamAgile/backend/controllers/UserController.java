package com.teamAgile.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.models.User;
import com.teamAgile.backend.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
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
	public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginRequest) {
		
		if (loginRequest.get("username") == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a username");
		if (loginRequest.get("password") == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a password");
		
		User user = userService.signIn(loginRequest.get("username"), loginRequest.get("password"));

		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
		}

		return ResponseEntity.ok(user);
	}

	// sent password request request
	
	// confirm password request code

}
