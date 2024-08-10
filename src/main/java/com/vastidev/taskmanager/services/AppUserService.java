package com.vastidev.taskmanager.services;


import com.vastidev.taskmanager.exceptions.AppUserNotFoundException;
import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final AppUserRepository userRepository;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AppUser save(AppUserDto userDto) {
        log.info("Saving user with username: {}", userDto.username());
        AppUser newAppUser = new AppUser(userDto);
        newAppUser.setPassword(passwordEncoder().encode(newAppUser.getPassword()));
        AppUser savedUser = userRepository.save(newAppUser);
        log.info("User saved with ID: {}", savedUser.getId());
        return savedUser;
    }

    public AppUser findById(UUID idUser) {
        log.info("Fetching user with ID: {}", idUser);
        Optional<AppUser> userOptional = userRepository.findById(idUser);
        if (userOptional.isPresent()) {
            log.info("User found with ID: {}", idUser);
            return userOptional.get();
        } else {
            getError(idUser);
            throw new AppUserNotFoundException(idUser);
        }
    }

    public List<AppUser> getAll() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }


    public void deleteById(UUID idUser) {
        log.info("Deleting user with ID: {}", idUser);
        Optional<AppUser> appUser = userRepository.findById(idUser);
        if (appUser.isPresent()) {
            userRepository.deleteById(idUser);
            log.info("User deleted with ID: {}", idUser);
        } else {
            getError(idUser);
            throw new AppUserNotFoundException(idUser);
        }
    }

    public AppUser updateById(UUID idUser, AppUserDto userDto) {
        log.info("Updating user with ID: {}", idUser);
        AppUser user = userRepository.findById(idUser).orElseThrow(() -> {
            getError(idUser);
            return new AppUserNotFoundException(idUser);
        });
        user.updateFromDto(userDto);
        AppUser updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
        return updatedUser;
    }
    private static void getError(UUID idUser) {
        log.error("User not found with ID: {}", idUser);
    }
}
