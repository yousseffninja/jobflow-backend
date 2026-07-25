package com.scar.jobflow_backend.job.dto;

import com.scar.jobflow_backend.job.JobStatus;
import com.scar.jobflow_backend.job.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record JobResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String companyLogoUrl,
        String title,
        String description,
        String sourceUrl,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String currency,
        JobStatus currentStatus,
        Priority priority,
        LocalDateTime appliedAt,
        LocalDateTime createdAt
) {}