package com.teamAgile.backend.util;

public class CreditCardValidator {
	public static boolean isValidCreditCard(String cardNum) {
		if (cardNum == null) {
			return false;
		}

		cardNum = cardNum.replaceAll("[\\s\\-()]", "");

		if (!cardNum.matches("\\d+")) {
			return false;
		}

		int sum = 0;
		boolean alternate = false;

		for (int i = cardNum.length() - 1; i >= 0; i--) {
			int digit = cardNum.charAt(i) - '0';

			if (alternate) {
				digit *= 2;
				if (digit > 9) {
					digit -= 9;
				}
			}

			sum += digit;
			alternate = !alternate;
		}

		return (sum % 10 == 0);
	}
}
