package com.vastidev.taskmanager.services;

import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.repository.AppUserRepository;
import com.vastidev.taskmanager.repository.TaskRepository;
import com.vastidev.taskmanager.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    AppUserRepository userRepository;

    @Mock
    TaskAssembler taskAssembler;

    @InjectMocks
    private TaskService service;

    private UUID userId;
    private Task task;
    private TaskDto taskDto;

    private AppUser appUser;

    @BeforeEach
    void setup(){
        userId = UUID.randomUUID();
        appUser= new AppUser();
        appUser.setId(userId);
        appUser.setUsername("username");
        appUser.setPassword("password");
        appUser.setEmail("user@gmail.com");

        taskDto= new TaskDto("Title", "Description");

        task = new Task(taskDto, appUser);
        task.setId(UUID.randomUUID());
    }

    @Test
    void testSave(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(appUser));
        when(repository.save(any(Task.class))).thenReturn(task);

        Task savedTask = service.save(taskDto,userId);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Title");
        assertThat(savedTask.getDescription()).isEqualTo("Description");
        assertThat(savedTask.getUser()).isEqualTo(appUser);

    }

    @Test
    void testFindAll(){
        TaskDto newTaskDto = new TaskDto("Title2", "Description2");
        Task newTask = new Task(newTaskDto, appUser);
        newTask.setId(UUID.randomUUID());

        when(repository.findAll()).thenReturn(List.of(task, newTask));

        List<Task> result = service.findAll();

        assertThat(result).isNotEmpty();
        assertEquals(2, result.size());
    }

    @Test
    void testFindById(){
        when(repository.findById(task.getId())).thenReturn(Optional.ofNullable(task));
        when(taskAssembler.toModel(any(Task.class))).thenReturn((EntityModel.of(task)));


        Task savedTask = service.getById(task.getId());

        assertThat(savedTask).isNotNull();
        assertEquals("Title", savedTask.getTitle());
        assertEquals("Description", savedTask.getDescription());
    }

    @Test
    void deleteById(){
        when(repository.findById(task.getId())).thenReturn(Optional.of(task));

        service.deleteById(task.getId());

        verify(repository, times(1)).deleteById(task.getId());
    }

}
