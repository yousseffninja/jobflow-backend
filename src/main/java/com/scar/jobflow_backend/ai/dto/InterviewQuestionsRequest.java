package com.scar.jobflow_backend.ai.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InterviewQuestionsRequest(
        @NotNull UUID jobId
) {}