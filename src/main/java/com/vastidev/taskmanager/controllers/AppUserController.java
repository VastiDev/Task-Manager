package com.vastidev.taskmanager.controllers;

import com.vastidev.taskmanager.assembler.AppUserAssembler;
import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.services.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
public class AppUserController {
    @Autowired
    private AppUserService userService;

    @Autowired
    private AppUserAssembler appUserAssembler;

    @Autowired
    private TaskAssembler taskAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add new User")
    public ResponseEntity<EntityModel<AppUser>> addUser(@RequestBody AppUserDto userDto) {
        AppUser newUser = userService.save(userDto);
        EntityModel<AppUser> entityModel = appUserAssembler.toModel(newUser);
        return ResponseEntity.created(linkTo(methodOn(AppUserController.class)
                .getAll()).toUri()).body(entityModel);
    }

    @GetMapping("/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get User by Id")
    public EntityModel<AppUser> userById(@PathVariable UUID idUser) {
        AppUser foundUser = userService.findById(idUser);
        EntityModel<AppUser> entityModel = appUserAssembler.toModel(foundUser);
        return entityModel;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all Users")
    public CollectionModel<AppUser> getAll() {
        List<AppUser> userList = userService.getAll();
        return CollectionModel.of(userList, linkTo(methodOn(AppUserController.class).getAll()).withSelfRel());
    }

    @DeleteMapping("/{idUser}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete User by Id")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID idUser) {
        userService.deleteById(idUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update User by Id")
    public ResponseEntity<EntityModel<AppUser>> updateUser(@PathVariable UUID idUser, @RequestBody AppUserDto userDto) {
        AppUser updatedUser = userService.updateById(idUser, userDto);
        EntityModel<AppUser> entityModel = appUserAssembler.toModel(updatedUser);
        return ResponseEntity.ok(entityModel);
    }

}