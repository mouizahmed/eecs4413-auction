package com.teamAgile.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDTO {

	@NotBlank(message = "securityAnswer is required")
	private String securityAnswer;

	@NotBlank(message = "newPassword is required")
	private String newPassword;

	public ForgotPasswordDTO() {
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
