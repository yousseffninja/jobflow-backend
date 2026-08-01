package com.scar.jobflow_backend.ai.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AiGenerationResponse(
        UUID id,
        String type,
        String result,
        LocalDateTime createdAt
) {}
