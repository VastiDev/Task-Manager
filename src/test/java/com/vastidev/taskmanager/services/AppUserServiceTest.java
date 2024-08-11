package com.vastidev.taskmanager.services;

import com.vastidev.taskmanager.model.dtos.AppUserDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository repository;

    @InjectMocks
    private AppUserService service;


    private AppUserDto userDto;
    private AppUser newAppUser;

    @BeforeEach
    void setup(){
        userDto = new AppUserDto("username", "password", "user@gmail.com");

        newAppUser = new AppUser(userDto);
        newAppUser.setPassword(new BCryptPasswordEncoder().encode("password"));
    }

    @Test
    void testSave(){
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        AppUser savedUser = service.save(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("username");
        assertThat(new BCryptPasswordEncoder().matches("password", savedUser.getPassword())).isTrue();
    }

    @Test
    void testGetById(){
        UUID testUUID = UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321");

        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setId(testUUID);

        when(repository.findById(testUUID)).thenReturn(Optional.of(newAppUser));


        AppUser savedUser = service.findById(testUUID);

        assertThat(savedUser).isNotNull();
        assertEquals("username", savedUser.getUsername());
    }

    @Test
    void testGetAll(){
        AppUserDto userDto1 = new AppUserDto("usename1", "password1", "user1@gmail.com");
        AppUser plusUser = new AppUser(userDto1);

        when(repository.findAll()).thenReturn(List.of(newAppUser, plusUser));

        List<AppUser> userList = service.getAll();

        assertThat(userList).isNotEmpty();
        assertEquals(2, userList.size());
    }
    @Test
    void deleteById(){
        Optional<AppUser> appUser = Optional.of(newAppUser);

        when(repository.findById(UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321"))).thenReturn(appUser);
        doNothing().when(repository).deleteById(UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321"));

        service.deleteById(UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321"));

        verify(repository, times(1)).deleteById(UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321"));

    }

    @Test
    void updateById(){
        AppUserDto userDto1 = new AppUserDto("username1", "password1", "user1@gmail.com");
        AppUser updatedUser = new AppUser(userDto1);
        UUID testUUID = UUID.fromString("93761645-9cf0-4465-8f5c-de93952d9321");

        when(repository.findById(testUUID)).thenReturn(Optional.of(newAppUser));
        when(repository.save(any(AppUser.class))).thenReturn(updatedUser);

        AppUser user = service.updateById(testUUID, userDto1);

        assertThat(user).isNotNull();
        assertEquals("username1", user.getUsername());
        assertEquals("password1", user.getPassword());
        assertEquals("user1@gmail.com", user.getEmail());
    }

}

