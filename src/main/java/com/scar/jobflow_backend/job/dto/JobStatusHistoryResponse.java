package com.scar.jobflow_backend.job.dto;

import com.scar.jobflow_backend.job.JobStatus;

import java.time.LocalDateTime;

public record JobStatusHistoryResponse(
        JobStatus oldStatus,
        JobStatus newStatus,
        String note,
        LocalDateTime changedAt
) {}