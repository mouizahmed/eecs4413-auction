package com.teamAgile.backend.util;

import java.util.UUID;

public class ValidationUtil {

	public static boolean isValidUUID(String uuidString) {
		if (uuidString == null || uuidString.trim().isEmpty()) {
			return false;
		}

		try {
			UUID.fromString(uuidString);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static UUID parseUUID(String uuidString) {
		if (!isValidUUID(uuidString)) {
			return null;
		}
		return UUID.fromString(uuidString);
	}

	public static boolean isValidDouble(String numberString) {
		if (numberString == null || numberString.trim().isEmpty()) {
			return false;
		}

		try {
			Double.parseDouble(numberString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static Double parseDouble(String numberString) {
		if (!isValidDouble(numberString)) {
			return null;
		}
		return Double.parseDouble(numberString);
	}

	public static boolean isValidInteger(String numberString) {
		if (numberString == null || numberString.trim().isEmpty()) {
			return false;
		}

		try {
			Integer.parseInt(numberString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static Integer parseInteger(String numberString) {
		if (!isValidInteger(numberString)) {
			return null;
		}
		return Integer.parseInt(numberString);
	}

	public static String sanitizeString(String input) {
		if (input == null) {
			return null;
		}

		String sanitized = input.trim();

		sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
				.replaceAll("'", "&#x27;").replaceAll("/", "&#x2F;");

		return sanitized;
	}
}