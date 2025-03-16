package com.teamAgile.backend.DTO.hateoas;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.teamAgile.backend.DTO.UserResponseDTO;

@Relation(collectionRelation = "users", itemRelation = "user")
public class UserModel extends RepresentationModel<UserModel> {

    private final UserResponseDTO user;

    public UserModel(UserResponseDTO user) {
        this.user = user;
    }

    public UserResponseDTO getUser() {
        return user;
    }
}