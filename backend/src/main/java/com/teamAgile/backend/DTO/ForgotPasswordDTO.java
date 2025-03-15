package com.teamAgile.backend.DTO;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDTO {

	@NotBlank(message = "securityAnswer is required")
	private String securityAnswer;
	
	@NotBlank(message ="newPassword is required")
	private String newPassword;

	public ForgotPasswordDTO() {
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

}
