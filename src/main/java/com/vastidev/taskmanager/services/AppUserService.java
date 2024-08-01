package com.vastidev.taskmanager.services;

import com.vastidev.taskmanager.assembler.AppUserAssembler;
import com.vastidev.taskmanager.exceptions.AppUserNotFoundException;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.repository.AppUserRepository;
import com.vastidev.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserAssembler userAssembler;

    @Autowired
    private TaskRepository taskRepository;

    public AppUser save(AppUserDto userDto) {
        AppUser newAppUser = new AppUser(userDto);
        return userRepository.save(newAppUser);
    }


    public AppUser findById(UUID idUser) {
        Optional<AppUser> userOptional = userRepository.findById(idUser);
        if (userOptional.isPresent()) {
            return userAssembler.toModel(userOptional.get()).getContent();
        } else {
            throw new AppUserNotFoundException(idUser);
        }
    }

    public List<AppUser> getAll() {
        return userRepository.findAll();
    }


    public void deleteById(UUID idUser) {
        Optional<AppUser> appUser = userRepository.findById(idUser);
        if (appUser.isPresent()) {
            userRepository.deleteById(idUser);
        } else {
            throw new AppUserNotFoundException(idUser);
        }
    }


    public AppUser updateById(UUID idUser, AppUserDto userDto) {
        AppUser user = userRepository.findById(idUser).orElseThrow(() -> new AppUserNotFoundException(idUser));
        user.updateFromDto(userDto);
        return userRepository.save(user);
    }

}