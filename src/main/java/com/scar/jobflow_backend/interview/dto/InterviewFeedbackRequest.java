package com.scar.jobflow_backend.interview.dto;

import com.scar.jobflow_backend.interview.InterviewOutcome;
import jakarta.validation.constraints.NotNull;

public record InterviewFeedbackRequest(
        String feedback,
        @NotNull InterviewOutcome outcome
) {}
