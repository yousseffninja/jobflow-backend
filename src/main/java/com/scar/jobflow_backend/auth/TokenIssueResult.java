package com.scar.jobflow_backend.auth;

import com.scar.jobflow_backend.auth.dto.AuthResponse;

public record TokenIssueResult(AuthResponse authResponse, String rawRefreshToken) {}
