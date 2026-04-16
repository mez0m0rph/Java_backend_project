package com.panin.tasktracker.mapper;

import com.panin.tasktracker.dto.audit.AuditLogResponse;
import com.panin.tasktracker.dto.comment.CommentResponse;
import com.panin.tasktracker.dto.project.ProjectMemberResponse;
import com.panin.tasktracker.dto.project.ProjectResponse;
import com.panin.tasktracker.dto.task.TaskResponse;
import com.panin.tasktracker.dto.user.UserResponse;
import com.panin.tasktracker.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getFullName(), user.getRole(), user.getCreatedAt());
    }

    public ProjectMemberResponse toProjectMemberResponse(ProjectMember member) {
        return new ProjectMemberResponse(member.getId(), member.getUser().getId(), member.getUser().getUsername(), member.getUser().getFullName(), member.getProjectRole(), member.getJoinedAt());
    }

    public ProjectResponse toProjectResponse(Project project, List<ProjectMember> members) {
        return new ProjectResponse(
                project.getId(),
                project.getCode(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getFullName(),
                project.getCreatedAt(),
                members.stream().map(this::toProjectMemberResponse).toList()
        );
    }

    public TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getCode(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getProject().getId(),
                task.getProject().getCode(),
                task.getAssignee() == null ? null : task.getAssignee().getId(),
                task.getAssignee() == null ? null : task.getAssignee().getFullName(),
                task.getReporter().getId(),
                task.getReporter().getFullName(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDueDate()
        );
    }

    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getTask().getId(), comment.getAuthor().getId(), comment.getAuthor().getFullName(), comment.getContent(), comment.getCreatedAt());
    }

    public AuditLogResponse toAuditLogResponse(AuditLog auditLog) {
        return new AuditLogResponse(auditLog.getId(), auditLog.getActor().getId(), auditLog.getActor().getFullName(), auditLog.getAction(), auditLog.getMessage(), auditLog.getCreatedAt());
    }
}
