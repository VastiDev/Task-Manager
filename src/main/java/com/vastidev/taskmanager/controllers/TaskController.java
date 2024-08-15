package com.vastidev.taskmanager.controllers;

import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.services.TaskService;
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
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskAssembler taskAssembler;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a task for a User",
            description = "Create a new task associated with the user specified by the user ID. Requires JWT authentication."
    )
    public ResponseEntity<EntityModel<Task>> createNewTask(@PathVariable UUID userId, @RequestBody TaskDto taskDto){
        Task savedTask  = taskService.save(taskDto, userId);
        EntityModel<Task> entityModel = taskAssembler.toModel(savedTask);
        return ResponseEntity.created(linkTo(methodOn(TaskController.class)
                .getAll()).toUri()).body(entityModel);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all tasks",
            description = "Retrieve a list of all tasks in the system. Requires JWT authentication."
    )
    public CollectionModel<EntityModel<Task>> getAll() {
        List<EntityModel<Task>> tasks = taskService.findAll().stream()
                .map(taskAssembler::toModel)
                .toList();

        return CollectionModel.of(tasks,linkTo(methodOn(TaskController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get task By Id",
            description = "Retrieve the details of a specific task by its ID. Requires JWT authentication."
    )
    public EntityModel<Task> taskById(@PathVariable UUID id) {
        Task task = taskService.getById(id);
        return taskAssembler.toModel(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete task by Id",
            description = "Delete a task specified by its ID. Requires JWT authentication."
    )
    ResponseEntity<Void> deleteTask(@PathVariable UUID id){
        taskService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update task",
            description = "Update the details of a specific task by its ID. Requires JWT authentication."
    )
    ResponseEntity<EntityModel<Task>> updateTask(@PathVariable UUID id, @RequestBody TaskDto taskDto){
        Task updatedTask = taskService.updateById(id, taskDto);
        EntityModel<Task> entityModel = taskAssembler.toModel(updatedTask);
        return ResponseEntity.ok(entityModel);

    }

}
