package com.panin.tasktracker.service;

import com.panin.tasktracker.common.ProjectRole;
import com.panin.tasktracker.common.Role;
import com.panin.tasktracker.entity.Project;
import com.panin.tasktracker.entity.User;
import com.panin.tasktracker.exception.ForbiddenException;
import com.panin.tasktracker.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final ProjectMemberRepository projectMemberRepository;

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ROLE_ADMIN;
    }

    public void requireProjectAccess(User user, Project project) {
        if (isAdmin(user)) {
            return;
        }
        if (!projectMemberRepository.existsByProjectIdAndUserId(project.getId(), user.getId())) {
            throw new ForbiddenException("No access to this project");
        }
    }

    public void requireProjectManageAccess(User user, Project project) {
        if (isAdmin(user) || project.getOwner().getId().equals(user.getId())) {
            return;
        }
        var membership = projectMemberRepository.findByProjectIdAndUserId(project.getId(), user.getId())
                .orElseThrow(() -> new ForbiddenException("No access to this project"));
        if (membership.getProjectRole() != ProjectRole.OWNER && membership.getProjectRole() != ProjectRole.MANAGER) {
            throw new ForbiddenException("No permission to manage this project");
        }
    }
}
