package com.teamAgile.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.UserRepository;
import com.teamAgile.backend.util.BCryptHashing;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signUp(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return null;
        }
        return userRepository.save(newUser);
    }

    public User signIn(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) return null;

        User user = userOptional.get();
        if (BCryptHashing.checkPassword(password, user.getPassword()) == false) return null;
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
