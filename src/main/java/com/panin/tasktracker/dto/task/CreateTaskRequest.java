package com.panin.tasktracker.dto.task;

import com.panin.tasktracker.common.TaskPriority;
import com.panin.tasktracker.common.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateTaskRequest(
        @NotBlank @Size(min = 3, max = 200) String title,
        @Size(max = 5000) String description,
        @NotNull TaskPriority priority,
        TaskStatus status,
        Long assigneeId,
        Instant dueDate
) {
}
