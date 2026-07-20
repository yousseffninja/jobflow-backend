package com.scar.jobflow_backend.auth.dto;

public record AuthResponse(
        String accessToken,
        UserSummary user
) {}
