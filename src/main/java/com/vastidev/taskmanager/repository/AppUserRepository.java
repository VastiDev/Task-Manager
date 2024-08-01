package com.vastidev.taskmanager.repository;

import com.vastidev.taskmanager.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
}
