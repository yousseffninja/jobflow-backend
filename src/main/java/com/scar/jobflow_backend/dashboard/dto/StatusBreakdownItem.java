package com.scar.jobflow_backend.dashboard.dto;

import com.scar.jobflow_backend.job.JobStatus;

public record StatusBreakdownItem(
        JobStatus status,
        long count
) {}
