package com.vastidev.taskmanager.services;

import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.exceptions.AppUserNotFoundException;
import com.vastidev.taskmanager.exceptions.TaskNotFoundException;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.repository.AppUserRepository;
import com.vastidev.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAssembler taskAssembler;

    @Autowired
    private AppUserRepository userRepository;

    public Task save(TaskDto taskDto, UUID idUser) {
        AppUser user = userRepository.findById(idUser)
                .orElseThrow(()-> new AppUserNotFoundException(idUser));
        Task newTask = new Task(taskDto, user);
        return taskRepository.save(newTask);
    }

    public List<Task> findAll() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

    }

    public Task getById(UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()){
            return taskAssembler.toModel(task.get()).getContent();
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    public void deleteById(UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()){
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException(id);
        }
    }


    public Task updateById(UUID id, TaskDto taskDto) {
        Task newTask = taskRepository.findById(id).orElseThrow(()-> new TaskNotFoundException(id));
        newTask.updateFromDto(taskDto);
        return taskRepository.save(newTask);
    }

}
