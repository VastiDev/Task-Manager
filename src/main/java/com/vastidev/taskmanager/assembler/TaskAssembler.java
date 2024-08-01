package com.vastidev.taskmanager.assembler;

import com.vastidev.taskmanager.controllers.TaskController;
import com.vastidev.taskmanager.model.entity.Task;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TaskAssembler implements RepresentationModelAssembler<Task, EntityModel<Task>> {

    @Override
    public EntityModel<Task> toModel(Task task) {
        return EntityModel.of(task,
                linkTo(methodOn(TaskController.class).taskById(task.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).getAll()).withRel("tasks"));
    }
}
