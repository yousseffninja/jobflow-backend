package com.scar.jobflow_backend.dashboard.dto;

import com.scar.jobflow_backend.job.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentActivityItem(
        UUID jobId,
        String jobTitle,
        String companyName,
        JobStatus oldStatus,
        JobStatus newStatus,
        String note,
        LocalDateTime changedAt
) {}
