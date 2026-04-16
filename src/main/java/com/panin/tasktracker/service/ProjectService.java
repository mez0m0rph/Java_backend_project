package com.panin.tasktracker.service;

import com.panin.tasktracker.common.AuditAction;
import com.panin.tasktracker.dto.project.AddProjectMemberRequest;
import com.panin.tasktracker.dto.project.CreateProjectRequest;
import com.panin.tasktracker.dto.project.ProjectMemberResponse;
import com.panin.tasktracker.dto.project.ProjectResponse;
import com.panin.tasktracker.entity.Project;
import com.panin.tasktracker.entity.ProjectMember;
import com.panin.tasktracker.entity.User;
import com.panin.tasktracker.exception.BadRequestException;
import com.panin.tasktracker.exception.NotFoundException;
import com.panin.tasktracker.mapper.AppMapper;
import com.panin.tasktracker.repository.ProjectMemberRepository;
import com.panin.tasktracker.repository.ProjectRepository;
import com.panin.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AccessService accessService;
    private final AuditService auditService;
    private final AppMapper mapper;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        if (projectRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new BadRequestException("Project code already exists");
        }
        User currentUser = currentUserService.getCurrentUser();
        Project project = projectRepository.save(Project.builder()
                .code(request.code().trim().toUpperCase())
                .name(request.name().trim())
                .description(request.description())
                .owner(currentUser)
                .createdAt(Instant.now())
                .build());
        ProjectMember ownerMember = projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .user(currentUser)
                .projectRole(com.panin.tasktracker.common.ProjectRole.OWNER)
                .joinedAt(Instant.now())
                .build());
        auditService.log(null, project, currentUser, AuditAction.PROJECT_CREATED, "Project created: " + project.getCode());
        return mapper.toProjectResponse(project, List.of(ownerMember));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects() {
        User currentUser = currentUserService.getCurrentUser();
        List<Project> projects = accessService.isAdmin(currentUser)
                ? projectRepository.findAll()
                : projectMemberRepository.findByUserId(currentUser.getId()).stream().map(ProjectMember::getProject).distinct().toList();
        return projects.stream().map(project -> mapper.toProjectResponse(project, projectMemberRepository.findByProjectId(project.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = getProject(id);
        accessService.requireProjectAccess(currentUser, project);
        return mapper.toProjectResponse(project, projectMemberRepository.findByProjectId(project.getId()));
    }

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, AddProjectMemberRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = getProject(projectId);
        accessService.requireProjectManageAccess(currentUser, project);
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException("User not found"));
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new BadRequestException("User already in project");
        }
        ProjectMember member = projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .user(user)
                .projectRole(request.projectRole())
                .joinedAt(Instant.now())
                .build());
        auditService.log(null, project, currentUser, AuditAction.MEMBER_ADDED, "Added member " + user.getUsername() + " as " + request.projectRole());
        return mapper.toProjectMemberResponse(member);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(Long projectId) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = getProject(projectId);
        accessService.requireProjectAccess(currentUser, project);
        return projectMemberRepository.findByProjectId(projectId).stream().map(mapper::toProjectMemberResponse).toList();
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }
}
