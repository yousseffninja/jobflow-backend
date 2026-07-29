package com.scar.jobflow_backend.dashboard;

import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.dashboard.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/status-breakdown")
    public ApiResponse<List<StatusBreakdownItem>> getStatusBreakdown() {
        return ApiResponse.success(dashboardService.getStatusBreakdown());
    }

    @GetMapping("/recent-activity")
    public ApiResponse<List<RecentActivityItem>> getRecentActivity(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ApiResponse.success(dashboardService.getRecentActivity(limit));
    }
}