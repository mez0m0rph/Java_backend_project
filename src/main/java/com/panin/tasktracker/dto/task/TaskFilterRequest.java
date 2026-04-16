package com.panin.tasktracker.dto.task;

import com.panin.tasktracker.common.TaskPriority;
import com.panin.tasktracker.common.TaskStatus;

public record TaskFilterRequest(
        Long projectId,
        Long assigneeId,
        TaskStatus status,
        TaskPriority priority,
        String search
) {
}
