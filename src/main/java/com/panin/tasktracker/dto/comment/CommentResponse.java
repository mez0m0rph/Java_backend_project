package com.panin.tasktracker.dto.comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long taskId,
        Long authorId,
        String authorName,
        String content,
        Instant createdAt
) {
}
