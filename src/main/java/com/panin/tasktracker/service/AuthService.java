package com.panin.tasktracker.service;

import com.panin.tasktracker.common.Role;
import com.panin.tasktracker.dto.auth.AuthResponse;
import com.panin.tasktracker.dto.auth.LoginRequest;
import com.panin.tasktracker.dto.auth.RegisterRequest;
import com.panin.tasktracker.entity.User;
import com.panin.tasktracker.exception.BadRequestException;
import com.panin.tasktracker.repository.UserRepository;
import com.panin.tasktracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }

        var role = userRepository.count() == 0 ? Role.ROLE_ADMIN : Role.ROLE_USER;
        User user = User.builder()
                .email(request.email().trim().toLowerCase())
                .username(request.username().trim())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName().trim())
                .role(role)
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().name()).build());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));
        User user = userRepository.findByUsername(request.login())
                .or(() -> userRepository.findByEmail(request.login()))
                .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().name()).build());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
