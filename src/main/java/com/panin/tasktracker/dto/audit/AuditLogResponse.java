package com.panin.tasktracker.dto.audit;

import com.panin.tasktracker.common.AuditAction;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        Long actorId,
        String actorName,
        AuditAction action,
        String message,
        Instant createdAt
) {
}
