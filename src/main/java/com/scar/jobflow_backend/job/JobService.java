package com.scar.jobflow_backend.job;

import com.scar.jobflow_backend.common.exception.ResourceNotFoundException;
import com.scar.jobflow_backend.company.Company;
import com.scar.jobflow_backend.company.CompanyRepository;
import com.scar.jobflow_backend.job.dto.*;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobStatusHistoryRepository historyRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobMapper jobMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JobResponse create(JobRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        Company company = companyRepository.findByIdAndUserId(request.companyId(), userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", request.companyId()));

        Job job = Job.builder()
                .user(user)
                .company(company)
                .title(request.title())
                .description(request.description())
                .sourceUrl(request.sourceUrl())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .currency(request.currency() != null ? request.currency() : "USD")
                .currentStatus(JobStatus.WISHLIST)
                .priority(request.priority())
                .build();

        Job saved = jobRepository.save(job);
        recordStatusChange(saved, null, JobStatus.WISHLIST, "Job created");

        return jobMapper.toResponse(saved);
    }

    public Page<JobResponse> search(
            JobStatus status, Priority priority, UUID companyId, String search, Pageable pageable
    ) {
        UUID userId = currentUserProvider.getCurrentUserId();
        return jobRepository.search(userId, status, priority, companyId, search, pageable)
                .map(jobMapper::toResponse);
    }

    public JobResponse getById(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", id));
        return jobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse update(UUID id, JobRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", id));

        Company company = companyRepository.findByIdAndUserId(request.companyId(), userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", request.companyId()));

        job.setCompany(company);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setSourceUrl(request.sourceUrl());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setCurrency(request.currency() != null ? request.currency() : job.getCurrency());
        job.setPriority(request.priority());

        Job saved = jobRepository.save(job);
        return jobMapper.toResponse(saved);
    }

    @Transactional
    public JobResponse updateStatus(UUID id, JobStatusUpdateRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", id));

        JobStatus oldStatus = job.getCurrentStatus();
        job.setCurrentStatus(request.status());

        if (request.status() == JobStatus.APPLIED && job.getAppliedAt() == null) {
            job.setAppliedAt(LocalDateTime.now());
        }

        Job saved = jobRepository.save(job);
        recordStatusChange(saved, oldStatus, request.status(), request.note());

        return jobMapper.toResponse(saved);
    }

    public List<JobStatusHistoryResponse> getStatusHistory(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", id));

        return historyRepository.findByJobIdOrderByChangedAtDesc(id).stream()
                .map(jobMapper::toHistoryResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", id));
        jobRepository.delete(job);
    }

    private void recordStatusChange(Job job, JobStatus oldStatus, JobStatus newStatus, String note) {
        JobStatusHistory history = JobStatusHistory.builder()
                .job(job)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .note(note)
                .build();
        historyRepository.save(history);
    }
}