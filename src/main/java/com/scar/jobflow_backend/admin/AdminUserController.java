package com.scar.jobflow_backend.admin;

import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.user.UserRepository;
import com.scar.jobflow_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    public record AdminUserView(UUID id, String email, String fullName, String role, boolean emailVerified, LocalDateTime createdAt) {}

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AdminUserView>> listAllUsers() {
        List<AdminUserView> users = userRepository.findAll().stream()
                .map(u -> new AdminUserView(u.getId(), u.getEmail(), u.getFullName(), u.getRole().name(), u.isEmailVerified(), u.getCreatedAt()))
                .toList();
        return ApiResponse.success(users);
    }
}