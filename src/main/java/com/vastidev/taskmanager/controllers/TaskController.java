package com.vastidev.taskmanager.controllers;

import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.services.TaskService;
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
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskAssembler taskAssembler;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a task for a User")
    public ResponseEntity<EntityModel<Task>> createNewTask(@PathVariable UUID userId, @RequestBody TaskDto taskDto){
        Task savedTask  = taskService.save(taskDto, userId);
        EntityModel<Task> entityModel = taskAssembler.toModel(savedTask);
        return ResponseEntity.created(linkTo(methodOn(TaskController.class)
                .getAll()).toUri()).body(entityModel);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tasks")
    public CollectionModel<EntityModel<Task>> getAll() {
        List<EntityModel<Task>> tasks = taskService.findAll().stream()
                .map(taskAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(tasks,linkTo(methodOn(TaskController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get task By Id")
    public EntityModel<Task> taskById(@PathVariable UUID id) {
        Task task = taskService.getById(id);
        EntityModel<Task> entityModel = taskAssembler.toModel(task);
        return entityModel;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete by Id")
    ResponseEntity<Void> deleteTask(@PathVariable UUID id){
        taskService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update task")
    ResponseEntity<EntityModel<Task>> updateTask(@PathVariable UUID id, @RequestBody TaskDto taskDto){
        Task updatedTask = taskService.updateById(id, taskDto);
        EntityModel<Task> entityModel = taskAssembler.toModel(updatedTask);
        return ResponseEntity.ok(entityModel);

    }

}
