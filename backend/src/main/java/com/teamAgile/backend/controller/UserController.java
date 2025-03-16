package com.teamAgile.backend.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.teamAgile.backend.DTO.ApiResponse;
import com.teamAgile.backend.DTO.AuctionItemResponseDTO;
import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.DTO.SignInDTO;
import com.teamAgile.backend.DTO.SignUpDTO;
import com.teamAgile.backend.DTO.UserResponseDTO;
import com.teamAgile.backend.DTO.hateoas.UserModel;
import com.teamAgile.backend.DTO.hateoas.UserModelAssembler;
import com.teamAgile.backend.exception.UserNotFoundException;
import com.teamAgile.backend.model.AuctionItem;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.AuctionService;
import com.teamAgile.backend.service.UserService;
import com.teamAgile.backend.util.ResponseUtil;
import com.teamAgile.backend.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final UserModelAssembler userModelAssembler;
	private final AuctionService auctionService;

	@Autowired
	public UserController(UserService userService, AuthenticationManager authenticationManager,
			UserModelAssembler userModelAssembler, AuctionService auctionService) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.userModelAssembler = userModelAssembler;
		this.auctionService = auctionService;
	}

	@GetMapping("/get-all")
	public ResponseEntity<ApiResponse<CollectionModel<UserModel>>> getAllUsers() {
		try {
			List<User> users = userService.getAllUsers();
			List<UserResponseDTO> userDTOs = users.stream().map(UserResponseDTO::new).collect(Collectors.toList());

			List<UserModel> userModels = userDTOs.stream().map(userModelAssembler::toModel)
					.collect(Collectors.toList());

			CollectionModel<UserModel> collectionModel = CollectionModel.of(userModels,
					linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

			return ResponseUtil.ok(collectionModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving users: " + e.getMessage());
		}
	}

	@PostMapping("/sign-up")
	public ResponseEntity<ApiResponse<UserResponseDTO>> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {
		try {
			if (signUpDTO.getPassword() == null || signUpDTO.getPassword().length() < 8) {
				return ResponseUtil.badRequest("Password must be at least 8 characters long");
			}

			String username = ValidationUtil.sanitizeString(signUpDTO.getUsername());
			if (username == null || username.isEmpty()) {
				return ResponseUtil.badRequest("Username cannot be empty");
			}

			User userObj = new User(signUpDTO);
			User createdUser = userService.signUp(userObj);
			UserResponseDTO userResponseDTO = new UserResponseDTO(createdUser);
			return ResponseUtil.created("User registered successfully", userResponseDTO);
		} catch (Exception e) {
			// Handle specific exceptions
			if (e.getMessage() != null && e.getMessage().contains("Username already taken")) {
				return ResponseUtil.conflict(e.getMessage());
			}
			return ResponseUtil.internalError("Error during registration: " + e.getMessage());
		}
	}

	@PostMapping("/sign-in")
	public ResponseEntity<ApiResponse<UserResponseDTO>> signIn(@Valid @RequestBody SignInDTO signInDTO,
			HttpServletRequest request) {
		try {
			String username = ValidationUtil.sanitizeString(signInDTO.getUsername());
			String password = signInDTO.getPassword();

			if (username == null || username.isEmpty()) {
				return ResponseUtil.badRequest("Username cannot be empty");
			}

			if (password == null || password.isEmpty()) {
				return ResponseUtil.badRequest("Password cannot be empty");
			}

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			User user = userService.signIn(username, password);
			if (user == null) {
				return ResponseUtil.unauthorized("Invalid username or password");
			}

			UserResponseDTO userResponseDTO = new UserResponseDTO(user);

			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			session.setAttribute("user", userResponseDTO);
			session.setMaxInactiveInterval(30 * 60); // 30 minutes

			return ResponseUtil.ok("Login successful", userResponseDTO);
		} catch (BadCredentialsException e) {
			return ResponseUtil.unauthorized("Invalid username or password");
		} catch (UserNotFoundException e) {
			return ResponseUtil.notFound(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("An error occurred during login: " + e.getMessage());
		}
	}

	@PostMapping("/sign-out")
	public ResponseEntity<ApiResponse<Void>> signOut(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
				SecurityContextHolder.clearContext();
				return ResponseUtil.ok("Successfully signed out", null);
			}

			return ResponseUtil.badRequest("No active session found");
		} catch (Exception e) {
			return ResponseUtil.internalError("Error during sign out: " + e.getMessage());
		}
	}

	@GetMapping("/get-security-question")
	public ResponseEntity<ApiResponse<String>> getSecurityQuestion(@RequestParam("username") String username) {
		try {
			// Sanitize input
			String sanitizedUsername = ValidationUtil.sanitizeString(username);
			if (sanitizedUsername == null || sanitizedUsername.isEmpty()) {
				return ResponseUtil.badRequest("Username cannot be empty");
			}

			String securityQuestion = userService.findSecurityQuestionByUsername(sanitizedUsername);
			return ResponseUtil.ok(securityQuestion);
		} catch (UserNotFoundException e) {
			return ResponseUtil.notFound(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving security question: " + e.getMessage());
		}
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Boolean>> validateSecurityAnswer(@RequestParam("username") String username,
			@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
		try {
			String sanitizedUsername = ValidationUtil.sanitizeString(username);
			if (sanitizedUsername == null || sanitizedUsername.isEmpty()) {
				return ResponseUtil.badRequest("Username cannot be empty");
			}

			if (forgotPasswordDTO.getNewPassword() == null || forgotPasswordDTO.getNewPassword().length() < 8) {
				return ResponseUtil.badRequest("New password must be at least 8 characters long");
			}

			boolean result = userService.validateSecurityAnswer(sanitizedUsername, forgotPasswordDTO);
			if (result) {
				return ResponseUtil.ok("Password reset successful", true);
			} else {
				return ResponseUtil.badRequest("Invalid security answer");
			}
		} catch (UserNotFoundException e) {
			return ResponseUtil.notFound(e.getMessage());
		} catch (Exception e) {
			return ResponseUtil.internalError("Error during password reset: " + e.getMessage());
		}
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserModel>> getUserById(@PathVariable UUID userId) {
		try {
			User user = userService.getUserById(userId);
			if (user == null) {
				return ResponseUtil.notFound("User not found with ID: " + userId);
			}

			UserResponseDTO userResponseDTO = new UserResponseDTO(user);
			UserModel userModel = userModelAssembler.toModel(userResponseDTO);

			return ResponseUtil.ok(userModel);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving user: " + e.getMessage());
		}
	}

	@GetMapping("/unpaid-items")
	public ResponseEntity<ApiResponse<List<AuctionItemResponseDTO>>> getUnpaidItems(HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			if (currentUser == null) {
				return ResponseUtil.unauthorized("User not authenticated");
			}

			List<AuctionItem> unpaidItems = auctionService.getUnpaidItemsForUser(currentUser);
			List<AuctionItemResponseDTO> unpaidItemDTOs = unpaidItems.stream()
					.map(AuctionItemResponseDTO::fromAuctionItem)
					.collect(Collectors.toList());

			return ResponseUtil.ok("Retrieved unpaid items successfully", unpaidItemDTOs);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error retrieving unpaid items: " + e.getMessage());
		}
	}
}
