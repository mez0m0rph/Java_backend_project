package com.panin.tasktracker.controller;

import com.panin.tasktracker.dto.project.AddProjectMemberRequest;
import com.panin.tasktracker.dto.project.CreateProjectRequest;
import com.panin.tasktracker.dto.project.ProjectMemberResponse;
import com.panin.tasktracker.dto.project.ProjectResponse;
import com.panin.tasktracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @GetMapping
    public List<ProjectResponse> getMyProjects() {
        return projectService.getMyProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse addMember(@PathVariable Long id, @Valid @RequestBody AddProjectMemberRequest request) {
        return projectService.addMember(id, request);
    }

    @GetMapping("/{id}/members")
    public List<ProjectMemberResponse> getMembers(@PathVariable Long id) {
        return projectService.getMembers(id);
    }
}
