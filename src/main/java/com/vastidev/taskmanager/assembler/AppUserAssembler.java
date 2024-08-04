package com.vastidev.taskmanager.assembler;


import com.vastidev.taskmanager.controllers.AppUserController;
import com.vastidev.taskmanager.model.dtos.AppUserResponse;
import com.vastidev.taskmanager.model.entity.AppUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AppUserAssembler implements RepresentationModelAssembler<AppUser, EntityModel<AppUserResponse>> {
    @Override
    public EntityModel<AppUserResponse> toModel(AppUser appUser) {
        AppUserResponse userResponse = new AppUserResponse(appUser.getId().toString(), appUser.getUsername(), appUser.getEmail());

        return EntityModel.of(userResponse,
                linkTo(methodOn(AppUserController.class).userById(appUser.getId())).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAll()).withRel("All users"));
    }
}

