package com.scar.jobflow_backend.auth;

import com.scar.jobflow_backend.auth.dto.AuthResponse;
import com.scar.jobflow_backend.auth.dto.ConfirmEmailRequest;
import com.scar.jobflow_backend.auth.dto.RegisterRequest;
import com.scar.jobflow_backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User Register successfully", response));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmEmail(
            @Valid @RequestBody ConfirmEmailRequest request
    ) {
        authService.confirmEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }
}
