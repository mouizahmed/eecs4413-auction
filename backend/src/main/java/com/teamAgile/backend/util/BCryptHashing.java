package com.teamAgile.backend.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptHashing {
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // 12 rounds
    }

    public static boolean checkPassword(String password, String hashed) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (hashed == null || hashed.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        return BCrypt.checkpw(password, hashed);
    }
}