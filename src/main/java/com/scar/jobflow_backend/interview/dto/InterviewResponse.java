package com.scar.jobflow_backend.interview.dto;

import com.scar.jobflow_backend.interview.InterviewOutcome;
import com.scar.jobflow_backend.interview.InterviewType;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewResponse(
        UUID id,
        UUID jobId,
        String jobTitle,
        String companyName,
        String companyLogoUrl,
        InterviewType type,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String location,
        String interviewerName,
        String feedback,
        InterviewOutcome outcome
) {}
