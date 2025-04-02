package com.teamAgile.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.teamAgile.backend.model.Rating;
import com.teamAgile.backend.model.User;
import com.teamAgile.backend.service.RatingService;
import com.teamAgile.backend.DTO.RatingRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ratings")
@Validated
public class RatingController extends BaseController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@Valid @RequestBody RatingRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
        	System.out.println("POST");
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }

            Rating rating = ratingService.createRating(
                    request.getRatedUserId(),
                    currentUser.getUserID(),
                    request.getRating(),
                    request.getFeedback());
            return ResponseEntity.ok(rating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Rating>> getUserRatings(@RequestParam("userID") UUID userID) {
        try {
            List<Rating> ratings = ratingService.getUserRatings(userID);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/average")
    public ResponseEntity<Double> getAverageUserRating(@RequestParam("userID") UUID userID) {
        try {
            double average = ratingService.getAverageUserRating(userID);
            return ResponseEntity.ok(average);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/total")
    public ResponseEntity<Long> getTotalUserRatings(@RequestParam("userID") UUID userID) {
        try {
            long total = ratingService.getTotalUserRatings(userID);
            return ResponseEntity.ok(total);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}