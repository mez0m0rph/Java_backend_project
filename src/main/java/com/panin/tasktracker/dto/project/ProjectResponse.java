package com.panin.tasktracker.dto.project;

import java.time.Instant;
import java.util.List;

public record ProjectResponse(
        Long id,
        String code,
        String name,
        String description,
        Long ownerId,
        String ownerName,
        Instant createdAt,
        List<ProjectMemberResponse> members
) {
}
