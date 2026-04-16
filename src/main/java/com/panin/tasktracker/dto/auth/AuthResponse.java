package com.panin.tasktracker.dto.auth;

import com.panin.tasktracker.common.Role;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String email,
        String fullName,
        Role role
) {
}
