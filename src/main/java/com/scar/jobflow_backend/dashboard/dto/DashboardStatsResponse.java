package com.scar.jobflow_backend.dashboard.dto;

public record DashboardStatsResponse(
        long totalApplications,
        double responseRate,
        double interviewRate,
        double offerRate
) {}
