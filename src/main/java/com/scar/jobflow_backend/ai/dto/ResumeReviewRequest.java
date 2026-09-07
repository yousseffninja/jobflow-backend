package com.scar.jobflow_backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResumeReviewRequest(
        @NotNull UUID jobId,
        @NotBlank String resumeText
) {}