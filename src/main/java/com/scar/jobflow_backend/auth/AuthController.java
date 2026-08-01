package com.scar.jobflow_backend.auth;

import com.scar.jobflow_backend.auth.dto.*;
import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.security.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse httpResponse
    ) {
        TokenIssueResult result = authService.register(request);
        setRefreshCookie(httpResponse, result.rawRefreshToken());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", result.authResponse()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmEmail(
            @Valid @RequestBody ConfirmEmailRequest request
    ) {
        authService.confirmEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        TokenIssueResult result = authenticationService.login(request);
        setRefreshCookie(httpResponse, result.rawRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(result.authResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenIssueResult result = authenticationService.refresh(refreshToken);
        setRefreshCookie(httpResponse, result.rawRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(result.authResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        authenticationService.logout(refreshToken);
        clearRefreshCookie(httpResponse);

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("If that email exists, a reset code has been sent", null)
        );
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<ApiResponse<Void>> verifyResetCode(
            @Valid @RequestBody VerifyResetCodeRequest request
    ) {
        passwordResetService.verifyResetCode(request);
        return ResponseEntity.ok(ApiResponse.success("Code verified", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    private void setRefreshCookie(HttpServletResponse httpResponse, String rawRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", rawRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse httpResponse) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}