package com.panin.tasktracker.dto.project;

import com.panin.tasktracker.common.ProjectRole;

import java.time.Instant;

public record ProjectMemberResponse(
        Long id,
        Long userId,
        String username,
        String fullName,
        ProjectRole projectRole,
        Instant joinedAt
) {
}
