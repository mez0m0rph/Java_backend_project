package com.panin.tasktracker.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank @Size(min = 2, max = 100) String code,
        @NotBlank @Size(min = 3, max = 150) String name,
        @Size(max = 2000) String description
) {
}
