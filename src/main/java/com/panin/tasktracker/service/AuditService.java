package com.panin.tasktracker.service;

import com.panin.tasktracker.common.AuditAction;
import com.panin.tasktracker.entity.AuditLog;
import com.panin.tasktracker.entity.Project;
import com.panin.tasktracker.entity.Task;
import com.panin.tasktracker.entity.User;
import com.panin.tasktracker.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(Task task, Project project, User actor, AuditAction action, String message) {
        auditLogRepository.save(AuditLog.builder()
                .task(task)
                .project(project)
                .actor(actor)
                .action(action)
                .message(message)
                .createdAt(Instant.now())
                .build());
    }
}
