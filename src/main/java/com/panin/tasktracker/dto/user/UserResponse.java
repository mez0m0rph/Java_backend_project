package com.panin.tasktracker.dto.user;

import com.panin.tasktracker.common.Role;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String username,
        String fullName,
        Role role,
        Instant createdAt
) {
}
