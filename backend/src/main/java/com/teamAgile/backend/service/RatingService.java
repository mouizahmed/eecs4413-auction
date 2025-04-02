package com.teamAgile.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamAgile.backend.model.Rating;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.repository.RatingRepository;
import com.teamAgile.backend.repository.UserRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public Rating createRating(UUID ratedUserId, UUID raterUserId, Integer rating, String feedback) {
        User ratedUser = userRepository.findById(ratedUserId)
                .orElseThrow(() -> new RuntimeException("Rated user not found"));
        User raterUser = userRepository.findById(raterUserId)
                .orElseThrow(() -> new RuntimeException("Rater user not found"));

        if (ratedUserId.equals(raterUserId)) {
            throw new RuntimeException("Cannot rate yourself");
        }

        Rating newRating = new Rating(ratedUser, raterUser, rating, feedback);
        return ratingRepository.save(newRating);
    }

    public List<Rating> getUserRatings(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepository.findByRatedUser(user);
    }

    public List<Rating> getUserRatingsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepository.findByRatedUser(user);
    }

    public double getAverageUserRating(UUID userId) {
        List<Rating> ratings = getUserRatings(userId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    public double getAverageUserRatingByUsername(String username) {
        List<Rating> ratings = getUserRatingsByUsername(username);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    public long getTotalUserRatings(UUID userId) {
        return getUserRatings(userId).size();
    }

    public long getTotalUserRatingsByUsername(String username) {
        return getUserRatingsByUsername(username).size();
    }


    public List<Rating> getRatingsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepository.findByRatedUser(user);
    }

    public double getAverageRatingByUsername(String username) {
        List<Rating> ratings = getRatingsByUsername(username);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }
}