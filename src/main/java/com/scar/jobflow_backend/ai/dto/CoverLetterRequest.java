package com.scar.jobflow_backend.ai.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CoverLetterRequest(
        @NotNull UUID jobId,
        String tone // e.g. "professional", "enthusiastic" — optional
) {}
