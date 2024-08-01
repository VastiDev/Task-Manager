package com.vastidev.taskmanager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();


      public AppUser(AppUserDto userDto) {
        this.username = userDto.username();
        this.password = userDto.password();
        this.email = userDto.email();
    }

    public void updateFromDto(AppUserDto userDto) {
        this.username = userDto.username();
        this.password = userDto.password();
        this.email = userDto.email();
    }
}

