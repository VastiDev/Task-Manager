package com.vastidev.taskmanager.assembler;

import com.vastidev.taskmanager.controllers.AppUserController;
import com.vastidev.taskmanager.model.entity.AppUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AppUserAssembler implements RepresentationModelAssembler<AppUser, EntityModel<AppUser>> {
    @Override
    public EntityModel<AppUser> toModel(AppUser appUser) {
        return EntityModel.of(appUser,
                linkTo(methodOn(AppUserController.class).userById(appUser.getId())).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAll()).withRel("All users"));
    }
}
