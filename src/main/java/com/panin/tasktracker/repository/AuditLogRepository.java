package com.panin.tasktracker.repository;

import com.panin.tasktracker.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    List<AuditLog> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
