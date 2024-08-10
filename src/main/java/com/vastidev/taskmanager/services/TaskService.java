package com.vastidev.taskmanager.services;

import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.exceptions.AppUserNotFoundException;
import com.vastidev.taskmanager.exceptions.TaskNotFoundException;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.repository.AppUserRepository;
import com.vastidev.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssembler taskAssembler;
    private final AppUserRepository userRepository;

    public Task save(TaskDto taskDto, UUID idUser) {
        log.info("Saving task for user with ID: {}", idUser);
        AppUser user = userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", idUser);
                    return new AppUserNotFoundException(idUser);
                });
        Task newTask = new Task(taskDto, user);
        Task savedTask = taskRepository.save(newTask);
        log.info("Task saved with ID: {}", savedTask.getId());
        return savedTask;
    }


    public List<Task> findAll() {
        log.info("Fetching all tasks");
        return taskRepository.findAll().stream().toList();
    }


    public Task getById(UUID id) {
        log.info("Fetching task with ID: {}", id);
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            log.info("Task found with ID: {}", id);
            return taskAssembler.toModel(task.get()).getContent();
        } else {
            getError(id);
            throw new TaskNotFoundException(id);
        }
    }



    public void deleteById(UUID id) {
        log.info("Deleting task with ID: {}", id);
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            log.info("Task deleted with ID: {}", id);
        } else {
            getError(id);
            throw new TaskNotFoundException(id);
        }
    }


    public Task updateById(UUID id, TaskDto taskDto) {
        log.info("Updating task with ID: {}", id);
        Task newTask = taskRepository.findById(id).orElseThrow(() -> {
            getError(id);
            return new TaskNotFoundException(id);
        });
        newTask.updateFromDto(taskDto);
        Task updatedTask = taskRepository.save(newTask);
        log.info("Task updated with ID: {}", updatedTask.getId());
        return updatedTask;
    }


    private static void getError(UUID id) {
        log.error("Task not found with ID: {}", id);
    }
}
