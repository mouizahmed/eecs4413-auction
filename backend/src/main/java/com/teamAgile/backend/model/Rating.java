package com.teamAgile.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ratingId", nullable = false)
    private UUID ratingID;

    @ManyToOne
    @JoinColumn(name = "ratedUserId", nullable = false)
    @JsonManagedReference(value = "user-ratings")
    private User ratedUser;

    @ManyToOne
    @JoinColumn(name = "raterUserId", nullable = false)
    @JsonManagedReference(value = "user-givenRatings")
    private User raterUser;

    @Column(name = "rating", nullable = false)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Column(name = "feedback", nullable = false, length = 500)
    private String feedback;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Rating() {
    }

    public Rating(User ratedUser, User raterUser, Integer rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (feedback == null || feedback.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback is required");
        }
        this.ratedUser = ratedUser;
        this.raterUser = raterUser;
        this.rating = rating;
        this.feedback = feedback;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getRatingID() {
        return ratingID;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public User getRaterUser() {
        return raterUser;
    }

    public void setRaterUser(User raterUser) {
        this.raterUser = raterUser;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        if (feedback == null || feedback.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback is required");
        }
        this.feedback = feedback;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}