package com.vastidev.taskmanager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;


    public Task(TaskDto taskDto, AppUser user) {
        this.title = taskDto.title();
        this.description = taskDto.description();
        this.createdAt = LocalDateTime.now();
        this.user = user;
    }


    public void updateFromDto(TaskDto taskDto) {
        this.title = taskDto.title();
        this.description = taskDto.description();
        this.createdAt = LocalDateTime.now();
    }
}

