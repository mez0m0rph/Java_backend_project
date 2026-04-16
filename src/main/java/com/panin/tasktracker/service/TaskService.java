package com.panin.tasktracker.service;

import com.panin.tasktracker.common.AuditAction;
import com.panin.tasktracker.common.TaskStatus;
import com.panin.tasktracker.dto.audit.AuditLogResponse;
import com.panin.tasktracker.dto.comment.CommentResponse;
import com.panin.tasktracker.dto.common.PageResponse;
import com.panin.tasktracker.dto.comment.CreateCommentRequest;
import com.panin.tasktracker.dto.task.CreateTaskRequest;
import com.panin.tasktracker.dto.task.TaskFilterRequest;
import com.panin.tasktracker.dto.task.TaskResponse;
import com.panin.tasktracker.dto.task.UpdateTaskRequest;
import com.panin.tasktracker.entity.Comment;
import com.panin.tasktracker.entity.Project;
import com.panin.tasktracker.entity.Task;
import com.panin.tasktracker.entity.User;
import com.panin.tasktracker.exception.BadRequestException;
import com.panin.tasktracker.exception.NotFoundException;
import com.panin.tasktracker.mapper.AppMapper;
import com.panin.tasktracker.repository.AuditLogRepository;
import com.panin.tasktracker.repository.CommentRepository;
import com.panin.tasktracker.repository.ProjectMemberRepository;
import com.panin.tasktracker.repository.TaskRepository;
import com.panin.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final CurrentUserService currentUserService;
    private final AccessService accessService;
    private final AuditService auditService;
    private final AppMapper mapper;

    @Transactional
    public TaskResponse create(Long projectId, CreateTaskRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = projectService.getProject(projectId);
        accessService.requireProjectAccess(currentUser, project);
        User assignee = resolveAssignee(projectId, request.assigneeId());
        Instant now = Instant.now();
        String code = project.getCode() + "-" + (taskRepository.countByProjectId(projectId) + 1);
        Task task = taskRepository.save(Task.builder()
                .code(code)
                .title(request.title().trim())
                .description(request.description())
                .priority(request.priority())
                .status(request.status() == null ? TaskStatus.TODO : request.status())
                .project(project)
                .assignee(assignee)
                .reporter(currentUser)
                .createdAt(now)
                .updatedAt(now)
                .dueDate(request.dueDate())
                .build());
        auditService.log(task, project, currentUser, AuditAction.TASK_CREATED, "Task created: " + task.getCode());
        if (assignee != null) {
            auditService.log(task, project, currentUser, AuditAction.ASSIGNEE_CHANGED, "Assignee set to " + assignee.getUsername());
        }
        return mapper.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse update(Long taskId, UpdateTaskRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTask(taskId);
        accessService.requireProjectAccess(currentUser, task.getProject());

        if (request.title() != null) {
            task.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.priority() != null && request.priority() != task.getPriority()) {
            var oldPriority = task.getPriority();
            task.setPriority(request.priority());
            auditService.log(task, task.getProject(), currentUser, AuditAction.PRIORITY_CHANGED, "Priority changed from " + oldPriority + " to " + request.priority());
        }
        if (request.status() != null && request.status() != task.getStatus()) {
            var oldStatus = task.getStatus();
            task.setStatus(request.status());
            auditService.log(task, task.getProject(), currentUser, AuditAction.STATUS_CHANGED, "Status changed from " + oldStatus + " to " + request.status());
        }
        if (request.assigneeId() != null) {
            User assignee = resolveAssignee(task.getProject().getId(), request.assigneeId());
            Long currentAssigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();
            if (!request.assigneeId().equals(currentAssigneeId)) {
                task.setAssignee(assignee);
                auditService.log(task, task.getProject(), currentUser, AuditAction.ASSIGNEE_CHANGED, "Assignee changed to " + assignee.getUsername());
            }
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        task.setUpdatedAt(Instant.now());
        auditService.log(task, task.getProject(), currentUser, AuditAction.TASK_UPDATED, "Task updated");
        return mapper.toTaskResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTask(taskId);
        accessService.requireProjectAccess(currentUser, task.getProject());
        return mapper.toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> search(TaskFilterRequest filter, int page, int size, String sortBy, String sortDir) {
        User currentUser = currentUserService.getCurrentUser();
        if (filter.projectId() != null) {
            accessService.requireProjectAccess(currentUser, projectService.getProject(filter.projectId()));
        }
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        var pageable = PageRequest.of(page, size, sort);
        var result = taskRepository.findAll(TaskSpecification.build(filter), pageable);
        var visible = result.getContent().stream().filter(task -> accessService.isAdmin(currentUser) || projectMemberRepository.existsByProjectIdAndUserId(task.getProject().getId(), currentUser.getId())).map(mapper::toTaskResponse).toList();
        return new PageResponse<>(visible, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isFirst(), result.isLast());
    }

    @Transactional
    public CommentResponse addComment(Long taskId, CreateCommentRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTask(taskId);
        accessService.requireProjectAccess(currentUser, task.getProject());
        Comment comment = commentRepository.save(Comment.builder()
                .task(task)
                .author(currentUser)
                .content(request.content().trim())
                .createdAt(Instant.now())
                .build());
        auditService.log(task, task.getProject(), currentUser, AuditAction.COMMENT_ADDED, "Comment added to task " + task.getCode());
        return mapper.toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTask(taskId);
        accessService.requireProjectAccess(currentUser, task.getProject());
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(mapper::toCommentResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getHistory(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTask(taskId);
        accessService.requireProjectAccess(currentUser, task.getProject());
        return auditLogRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream().map(mapper::toAuditLogResponse).toList();
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    private User resolveAssignee(Long projectId, Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        User assignee = userRepository.findById(assigneeId).orElseThrow(() -> new NotFoundException("Assignee not found"));
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assigneeId)) {
            throw new BadRequestException("Assignee is not a project member");
        }
        return assignee;
    }
}
