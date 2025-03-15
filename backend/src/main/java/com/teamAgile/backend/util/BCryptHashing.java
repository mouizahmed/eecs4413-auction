package com.teamAgile.backend.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptHashing {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // 12 rounds
    }

    public static boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}