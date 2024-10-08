package com.vastidev.taskmanager.controllers;

import com.vastidev.taskmanager.assembler.AppUserAssembler;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.dtos.AppUserResponse;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.services.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService userService;


    private final AppUserAssembler appUserAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add new User", description = "Step 1: Create a new user account. Step 2: go to login session above!")
    public ResponseEntity<EntityModel<AppUserResponse>> addUser(@RequestBody AppUserDto userDto) {
        AppUser newUser = userService.save(userDto);
        EntityModel<AppUserResponse> entityModel = appUserAssembler.toModel(newUser);
        return ResponseEntity.created(linkTo(methodOn(AppUserController.class)
                .getAll()).toUri()).body(entityModel);
    }

    @GetMapping("/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get User by Id", description = "Retrieve user details. Requires JWT generated in Step 2.")
    public EntityModel<AppUserResponse> userById(@PathVariable UUID idUser) {
        AppUser foundUser = userService.findById(idUser);
        return appUserAssembler.toModel(foundUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all Users", description = "Retrieve all users. Requires JWT generated in Step 2" )
    public CollectionModel<EntityModel<AppUserResponse>> getAll() {
        List<AppUser> userList = userService.getAll();
        return appUserAssembler.toCollectionModel(userList);
    }

    @DeleteMapping("/{idUser}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete User by Id", description = "Delete a user. Requires JWT generated in Step 2.")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID idUser) {
        userService.deleteById(idUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update User by Id", description = "Update a user's details. Requires JWT generated in Step 2.")
    public ResponseEntity<EntityModel<AppUserResponse>> updateUser(@PathVariable UUID idUser, @RequestBody AppUserDto userDto) {
        AppUser updatedUser = userService.updateById(idUser, userDto);
        EntityModel<AppUserResponse> entityModel = appUserAssembler.toModel(updatedUser);
        return ResponseEntity.ok(entityModel);
    }
}