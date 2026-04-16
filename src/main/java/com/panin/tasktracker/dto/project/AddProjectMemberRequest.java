package com.panin.tasktracker.dto.project;

import com.panin.tasktracker.common.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record AddProjectMemberRequest(
        @NotNull Long userId,
        @NotNull ProjectRole projectRole
) {
}
