package com.teamAgile.backend.repository;

import com.teamAgile.backend.model.Rating;
import com.teamAgile.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByRatedUser(User ratedUser);
}