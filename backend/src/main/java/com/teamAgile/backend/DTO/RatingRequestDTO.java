package com.teamAgile.backend.DTO;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for creating a new rating.
 * Used to transfer rating data from the frontend to the backend.
 */
public class RatingRequestDTO {
    @NotNull(message = "Rated user ID cannot be null")
    private UUID ratedUserId;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Feedback cannot be empty")
    private String feedback;

    public RatingRequestDTO() {
    }

    public RatingRequestDTO(UUID ratedUserId, Integer rating, String feedback) {
        this.ratedUserId = ratedUserId;
        this.rating = rating;
        this.feedback = feedback;
    }

    public UUID getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(UUID ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}