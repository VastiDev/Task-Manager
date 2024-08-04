package com.vastidev.taskmanager.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class AppUserResponse extends RepresentationModel<AppUserResponse> {
    private String id;
    private String username;
    private String email;

}
