package com.scar.jobflow_backend.dashboard;

import com.scar.jobflow_backend.dashboard.dto.*;
import com.scar.jobflow_backend.job.Job;
import com.scar.jobflow_backend.job.JobRepository;
import com.scar.jobflow_backend.job.JobStatus;
import com.scar.jobflow_backend.job.JobStatusHistory;
import com.scar.jobflow_backend.job.JobStatusHistoryRepository;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JobRepository jobRepository;
    private final JobStatusHistoryRepository historyRepository;
    private final CurrentUserProvider currentUserProvider;

    public DashboardStatsResponse getStats() {
        UUID userId = currentUserProvider.getCurrentUserId();
        Map<JobStatus, Long> counts = getStatusCountMap(userId);

        long wishlist = counts.getOrDefault(JobStatus.WISHLIST, 0L);
        long applied = counts.getOrDefault(JobStatus.APPLIED, 0L);
        long interviewing = counts.getOrDefault(JobStatus.INTERVIEWING, 0L);
        long offer = counts.getOrDefault(JobStatus.OFFER, 0L);
        long rejected = counts.getOrDefault(JobStatus.REJECTED, 0L);
        long withdrawn = counts.getOrDefault(JobStatus.WITHDRAWN, 0L);

        // "Applications" = jobs that moved past Wishlist (i.e. actually applied)
        long totalApplications = applied + interviewing + offer + rejected + withdrawn;
        long responded = interviewing + offer + rejected; // got any reply at all
        long reachedInterview = interviewing + offer;
        long offers = offer;

        return new DashboardStatsResponse(
                totalApplications,
                percentage(responded, totalApplications),
                percentage(reachedInterview, totalApplications),
                percentage(offers, totalApplications)
        );
    }

    public List<StatusBreakdownItem> getStatusBreakdown() {
        UUID userId = currentUserProvider.getCurrentUserId();
        Map<JobStatus, Long> counts = getStatusCountMap(userId);

        return java.util.Arrays.stream(JobStatus.values())
                .map(status -> new StatusBreakdownItem(status, counts.getOrDefault(status, 0L)))
                .toList();
    }

    public List<RecentActivityItem> getRecentActivity(int limit) {
        UUID userId = currentUserProvider.getCurrentUserId();
        List<JobStatusHistory> history = historyRepository.findRecentForUser(userId, PageRequest.of(0, limit));

        return history.stream()
                .map(h -> {
                    Job job = h.getJob();
                    return new RecentActivityItem(
                            job.getId(),
                            job.getTitle(),
                            job.getCompany().getName(),
                            h.getOldStatus(),
                            h.getNewStatus(),
                            h.getNote(),
                            h.getChangedAt()
                    );
                })
                .toList();
    }

    private Map<JobStatus, Long> getStatusCountMap(UUID userId) {
        Map<JobStatus, Long> map = new EnumMap<>(JobStatus.class);
        for (Object[] row : jobRepository.countByStatusForUser(userId)) {
            map.put((JobStatus) row[0], (Long) row[1]);
        }
        return map;
    }

    private double percentage(long part, long total) {
        if (total == 0) return 0.0;
        return Math.round((part * 1000.0) / total) / 10.0; // one decimal place
    }
}