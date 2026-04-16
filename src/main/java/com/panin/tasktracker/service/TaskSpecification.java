package com.panin.tasktracker.service;

import com.panin.tasktracker.dto.task.TaskFilterRequest;
import com.panin.tasktracker.entity.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> build(TaskFilterRequest filter) {
        return Specification.where(projectId(filter.projectId()))
                .and(assigneeId(filter.assigneeId()))
                .and(status(filter.status()))
                .and(priority(filter.priority()))
                .and(search(filter.search()));
    }

    private static Specification<Task> projectId(Long projectId) {
        return (root, query, cb) -> projectId == null ? null : cb.equal(root.get("project").get("id"), projectId);
    }

    private static Specification<Task> assigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? null : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private static Specification<Task> status(com.panin.tasktracker.common.TaskStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Task> priority(com.panin.tasktracker.common.TaskPriority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    private static Specification<Task> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("code")), pattern)
            );
        };
    }
}
