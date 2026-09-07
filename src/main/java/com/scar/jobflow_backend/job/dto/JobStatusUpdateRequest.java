package com.scar.jobflow_backend.job.dto;

import com.scar.jobflow_backend.job.JobStatus;
import jakarta.validation.constraints.NotNull;

public record JobStatusUpdateRequest(

        @NotNull(message = "Status is required")
        JobStatus status,

        String note
) {}
