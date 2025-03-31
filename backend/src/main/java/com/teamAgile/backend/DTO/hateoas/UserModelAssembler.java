package com.teamAgile.backend.DTO.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.teamAgile.backend.DTO.UserResponseDTO;
import com.teamAgile.backend.controller.UserController;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, UserModel> {

    @Override
    public UserModel toModel(UserResponseDTO user) {
        UserModel userModel = new UserModel(user);

        userModel.add(
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));

        return userModel;
    }
}