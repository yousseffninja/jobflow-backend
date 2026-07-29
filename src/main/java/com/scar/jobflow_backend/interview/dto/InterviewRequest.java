package com.scar.jobflow_backend.interview.dto;

import com.scar.jobflow_backend.interview.InterviewType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewRequest(
        @NotNull UUID jobId,
        @NotNull InterviewType type,
        @NotNull LocalDateTime scheduledAt,
        Integer durationMinutes,
        String location,
        String interviewerName
) {}
