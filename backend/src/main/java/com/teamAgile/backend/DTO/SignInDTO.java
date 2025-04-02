package com.teamAgile.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class SignInDTO {

	@NotBlank(message = "username is required")
	private String username;

	@NotBlank(message = "password is required")
	private String password;

	public SignInDTO() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
