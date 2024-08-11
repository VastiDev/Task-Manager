package com.vastidev.taskmanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vastidev.taskmanager.assembler.AppUserAssembler;
import com.vastidev.taskmanager.config.TestSecurityConfig;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.dtos.AppUserResponse;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppUserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class AppUserControllerTest {

    @MockBean
    private AppUserService userService;

    @MockBean
    private AppUserAssembler userAssembler;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AppUserController userController;


    private AppUser appUser;
    private AppUserDto appUserDto;
    private AppUserResponse appUserResponse;
    private UUID userId;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        appUserDto = new AppUserDto("username", "password", "user@gmail.com");
        appUser = new AppUser(appUserDto);
        appUser.setId(UUID.randomUUID());

        appUserResponse = new AppUserResponse(appUser.getId().toString(), appUser.getUsername(), appUser.getEmail());

        EntityModel<AppUser> entityModel = EntityModel.of(appUser,
                linkTo(methodOn(AppUserController.class).userById(appUser.getId())).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAll()).withRel("tasks"));
    }

    @Test
    void testCreate() throws Exception {
        when(userService.save(any(AppUserDto.class))).thenReturn(appUser);
        when(userAssembler.toModel(any(AppUser.class))).thenReturn(EntityModel.of(appUserResponse));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(appUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(appUser.getUsername()))
                .andExpect(jsonPath("$.email").value(appUser.getEmail()));
    }

    @Test
    void testGetAll(){
        userId = UUID.randomUUID();
        AppUserDto appUserDto2 = new AppUserDto("username2", "password2", "user2@gmail.com");
        AppUser newAppUser = new AppUser(appUserDto2);
        newAppUser.setId(UUID.randomUUID());
        List<AppUser> allUsers = List.of(appUser, newAppUser);

        when(userService.getAll()).thenReturn(allUsers);
        when(userAssembler.toModel(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser u = invocation.getArgument(0);
            return EntityModel.of(u,
                    linkTo(methodOn(AppUserController.class).userById(u.getId())).withSelfRel(),
                    linkTo(methodOn(AppUserController.class).getAll()).withRel("All users"));
        });
    }

    @Test
    void testFindById() throws Exception {
        when(userService.findById(userId)).thenReturn(appUser);
        when(userAssembler.toModel(any(AppUser.class))).thenReturn(EntityModel.of(appUserResponse));

        mockMvc.perform(get("/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(appUser.getUsername()))
                .andExpect(jsonPath("$.email").value(appUser.getEmail()));
    }

    @Test
    void deleteById() throws Exception {
        doNothing().when(userService).deleteById(userId);

        ResultActions response = mockMvc.perform(delete("/user/{userId}", userId));

        response.andDo(print())
                .andExpect(status().isNoContent());

    }

    @Test
    void updateById() throws Exception {
        userId = UUID.randomUUID();
        AppUserDto appUserDto2 = new AppUserDto("username2", "password2", "user2@gmail.com");
        AppUser updatedAppUser = new AppUser(appUserDto2);
        updatedAppUser.setId(UUID.randomUUID());

        AppUserResponse updatedAppUserResponse = new AppUserResponse(updatedAppUser.getId().toString(), updatedAppUser.getUsername(), updatedAppUser.getEmail());
        EntityModel<AppUserResponse> updatedEntityModel = EntityModel.of(updatedAppUserResponse,
                linkTo(methodOn(AppUserController.class).userById(userId)).withSelfRel());

        when(userService.updateById(any(UUID.class), any(AppUserDto.class))).thenReturn(updatedAppUser);
        when(userAssembler.toModel(any(AppUser.class))).thenReturn(updatedEntityModel);

        mockMvc.perform(put("/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(appUserDto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedAppUser.getUsername()))
                .andExpect(jsonPath("$.email").value(updatedAppUser.getEmail()));

    }

}
