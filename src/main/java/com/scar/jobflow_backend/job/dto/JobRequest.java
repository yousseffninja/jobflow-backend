package com.scar.jobflow_backend.job.dto;

import com.scar.jobflow_backend.job.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record JobRequest(

        @NotNull(message = "Company is required")
        UUID companyId,

        @NotBlank(message = "Job title is required")
        String title,

        String description,
        String sourceUrl,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String currency,

        @NotNull(message = "Priority is required")
        Priority priority
) {}