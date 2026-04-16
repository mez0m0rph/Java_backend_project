package com.panin.tasktracker.controller;

import com.panin.tasktracker.common.TaskPriority;
import com.panin.tasktracker.common.TaskStatus;
import com.panin.tasktracker.dto.audit.AuditLogResponse;
import com.panin.tasktracker.dto.comment.CommentResponse;
import com.panin.tasktracker.dto.comment.CreateCommentRequest;
import com.panin.tasktracker.dto.common.PageResponse;
import com.panin.tasktracker.dto.task.CreateTaskRequest;
import com.panin.tasktracker.dto.task.TaskFilterRequest;
import com.panin.tasktracker.dto.task.TaskResponse;
import com.panin.tasktracker.dto.task.UpdateTaskRequest;
import com.panin.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequest request) {
        return taskService.create(projectId, request);
    }

    @PatchMapping("/tasks/{taskId}")
    public TaskResponse update(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.update(taskId, request);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse getById(@PathVariable Long taskId) {
        return taskService.getById(taskId);
    }

    @GetMapping("/tasks")
    public PageResponse<TaskResponse> search(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return taskService.search(new TaskFilterRequest(projectId, assigneeId, status, priority, search), page, size, sortBy, sortDir);
    }

    @PostMapping("/tasks/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request) {
        return taskService.addComment(taskId, request);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long taskId) {
        return taskService.getComments(taskId);
    }

    @GetMapping("/tasks/{taskId}/history")
    public List<AuditLogResponse> getHistory(@PathVariable Long taskId) {
        return taskService.getHistory(taskId);
    }
}
