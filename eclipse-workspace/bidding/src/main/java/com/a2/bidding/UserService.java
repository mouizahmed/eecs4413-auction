package com.a2.bidding;
import java.util.ArrayList;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong userIdCounter = new AtomicLong(1);

    public User signUp(User user) {
        user.setId(userIdCounter.getAndIncrement());
        users.add(user);
        return user;
    }

    public User signIn(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}