package com.teamAgile.backend.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.DTO.SignInDTO;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {
		
		User userObj = new User(signUpDTO);
		
		User createdUser = userService.signUp(userObj);
		if (createdUser == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PostMapping("/sign-in")
	public ResponseEntity<?> signIn(@Valid @RequestBody SignInDTO signInDTO, HttpServletRequest request) {

		
		try {
			// Authenticate using Spring Security
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					signInDTO.getUsername(), signInDTO.getPassword()));

			// Set the authentication in the SecurityContext
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Get the authenticated user
			User user = userService.signIn(signInDTO.getUsername(), signInDTO.getPassword());

			// Create new session
			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			Object userObject = Map.of("userID", user.getUserID(), "username", user.getUsername(), "firstName", user.getFirstName(), "lastName", user.getLastName(), "streetNum", user.getStreetNum(), "streetName", user.getStreetName(), "postalCode", user.getPostalCode(), "city", user.getCity(), "country", user.getCountry());
			session.setAttribute("user", userObject);
			session.setMaxInactiveInterval(30 * 60); // 30 minutes

			return ResponseEntity.ok().body(userObject);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password.");
		}
	}

	@PostMapping("/sign-out")
	public ResponseEntity<?> signOut(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			SecurityContextHolder.clearContext();
			return ResponseEntity.ok().body("Successfully signed out");
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No active session found");
	}

	@GetMapping("/get-security-question")
	public ResponseEntity<?> getSecurityQuestion(@RequestParam("username") String username) {
	    String securityQuestion = userService.findSecurityQuestionByUsername(username);
	    if (securityQuestion == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }
	    return ResponseEntity.ok(securityQuestion);
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<?> validateSecurityAnswer(@RequestParam("username") String username, @Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
	    boolean securityQuestion = userService.validateSecurityAnswer(username, forgotPasswordDTO);
	    return ResponseEntity.ok(securityQuestion);
	}
}
