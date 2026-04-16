package com.panin.tasktracker.repository;

import com.panin.tasktracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByCode(String code);
    Optional<Project> findByCode(String code);
}
