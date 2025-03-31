package com.teamAgile.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.DTO.ForgotPasswordDTO;
import com.teamAgile.backend.exception.UserNotFoundException;
import com.teamAgile.backend.exception.UsernameAlreadyExistsException;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.UserRepository;
import com.teamAgile.backend.util.BCryptHashing;
import com.teamAgile.backend.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User signUp(User newUser) {
		if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}

		if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
			throw new UsernameAlreadyExistsException("Username already taken: " + newUser.getUsername());
		}

		if (newUser.getPassword() != null && !newUser.getPassword().startsWith("$2a$")) {
			newUser.setPassword(newUser.getPassword());
		}

		return userRepository.save(newUser);
	}

	public User signIn(String username, String password) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}

		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be empty");
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

		if (!BCryptHashing.checkPassword(password, user.getPassword())) {
			return null;
		}
		return user;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public String findSecurityQuestionByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}

		String sanitizedUsername = ValidationUtil.sanitizeString(username);

		User user = userRepository.findByUsername(sanitizedUsername)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + sanitizedUsername));

		return user.getSecurityQuestion();
	}

	public boolean validateSecurityAnswer(String username, ForgotPasswordDTO securityAnswerDTO) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}

		if (securityAnswerDTO == null) {
			throw new IllegalArgumentException("Security answer data cannot be null");
		}

		if (securityAnswerDTO.getSecurityAnswer() == null || securityAnswerDTO.getSecurityAnswer().trim().isEmpty()) {
			throw new IllegalArgumentException("Security answer cannot be empty");
		}

		if (securityAnswerDTO.getNewPassword() == null || securityAnswerDTO.getNewPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("New password cannot be empty");
		}

		String sanitizedUsername = ValidationUtil.sanitizeString(username);

		User user = userRepository.findByUsername(sanitizedUsername)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + sanitizedUsername));

		if (!securityAnswerDTO.getSecurityAnswer().toLowerCase().trim()
				.equals(user.getSecurityAnswer().toLowerCase().trim())) {
			return false;
		}

		user.setPassword(securityAnswerDTO.getNewPassword());
		userRepository.save(user);

		return true;
	}

	public User getUserById(UUID userId) {
		if (userId == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new UserNotFoundException("User not found with ID: " + userId);
		}

		return userOpt.get();
	}

	public User findByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
	}
}
