package com.vastidev.taskmanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vastidev.taskmanager.assembler.TaskAssembler;
import com.vastidev.taskmanager.config.TestSecurityConfig;
import com.vastidev.taskmanager.model.dtos.TaskDto;
import com.vastidev.taskmanager.model.entity.AppUser;
import com.vastidev.taskmanager.model.entity.Task;
import com.vastidev.taskmanager.repository.AppUserRepository;
import com.vastidev.taskmanager.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @MockBean
    private AppUserRepository userRepository;

    @MockBean
    private TaskAssembler assembler;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Task task;
    private TaskDto taskDto;
    private AppUser appUser;
    private UUID userId;
    private EntityModel<Task> entityModel;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        appUser = new AppUser();
        appUser.setId(userId);
        appUser.setUsername("username");
        appUser.setPassword("password");
        appUser.setEmail("user@gmail.com");

        taskDto = new TaskDto("Title", "Description");

        task = new Task(taskDto, appUser);
        task.setId(UUID.randomUUID());

        entityModel = EntityModel.of(task,
                linkTo(methodOn(TaskController.class).taskById(task.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).getAll()).withRel("tasks"));
    }

    @Test
    void testCreate() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(appUser));
        when(taskService.save(any(TaskDto.class), eq(userId))).thenReturn(task);
        when(assembler.toModel(any(Task.class))).thenReturn(entityModel);

        mockMvc.perform(post("/task/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.user.id").value(appUser.getId().toString()));

        verify(taskService, times(1)).save(any(TaskDto.class), eq(userId));
        verify(assembler, times(1)).toModel(any(Task.class));
    }

    @Test
    void testGetAll() throws Exception {
        TaskDto newTaskDto = new TaskDto("Title2", "Description2");
        Task addedTask = new Task(newTaskDto, appUser);
        addedTask.setId(UUID.randomUUID());

        List<Task> allTasks = List.of(task, addedTask);
        when(taskService.findAll()).thenReturn(allTasks);

        EntityModel<Task> addedTaskEntityModel = EntityModel.of(addedTask,
                linkTo(methodOn(TaskController.class).taskById(addedTask.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).getAll()).withRel("tasks"));
        when(assembler.toModel(task)).thenReturn(entityModel);
        when(assembler.toModel(addedTask)).thenReturn(addedTaskEntityModel);

        mockMvc.perform(get("/task")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.taskList",hasSize(2)))
                .andExpect(jsonPath("$._embedded.taskList[0].title").value(task.getTitle()))
                .andExpect(jsonPath("$._embedded.taskList[0].description").value(task.getDescription()))
                .andExpect(jsonPath("$._embedded.taskList[1].title").value(addedTask.getTitle()))
                .andExpect(jsonPath("$._embedded.taskList[1].description").value(addedTask.getDescription()));
        verify(taskService, times(1)).findAll();
        verify(assembler, times(1)).toModel(task);
        verify(assembler, times(1)).toModel(addedTask);


    }
}
