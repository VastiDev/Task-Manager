package com.vastidev.taskmanager.repository;

import com.vastidev.taskmanager.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID > {
}
