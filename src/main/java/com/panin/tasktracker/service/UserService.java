package com.panin.tasktracker.service;

import com.panin.tasktracker.dto.user.UserResponse;
import com.panin.tasktracker.exception.NotFoundException;
import com.panin.tasktracker.mapper.AppMapper;
import com.panin.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AppMapper mapper;
    private final CurrentUserService currentUserService;

    public UserResponse getCurrentUser() {
        return mapper.toUserResponse(currentUserService.getCurrentUser());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(mapper::toUserResponse).toList();
    }

    public UserResponse getById(Long id) {
        return userRepository.findById(id).map(mapper::toUserResponse).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
