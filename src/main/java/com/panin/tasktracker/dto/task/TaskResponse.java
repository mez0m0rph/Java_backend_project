package com.panin.tasktracker.dto.task;

import com.panin.tasktracker.common.TaskPriority;
import com.panin.tasktracker.common.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String code,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        String projectCode,
        Long assigneeId,
        String assigneeName,
        Long reporterId,
        String reporterName,
        Instant createdAt,
        Instant updatedAt,
        Instant dueDate
) {
}
