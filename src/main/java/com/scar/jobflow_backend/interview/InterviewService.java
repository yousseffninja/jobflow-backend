package com.scar.jobflow_backend.interview;

import com.scar.jobflow_backend.common.exception.ResourceNotFoundException;
import com.scar.jobflow_backend.interview.dto.InterviewFeedbackRequest;
import com.scar.jobflow_backend.interview.dto.InterviewRequest;
import com.scar.jobflow_backend.interview.dto.InterviewResponse;
import com.scar.jobflow_backend.job.Job;
import com.scar.jobflow_backend.job.JobRepository;
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
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final InterviewMapper interviewMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public InterviewResponse create(InterviewRequest request) {

        UUID userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findByIdAndUserId(request.jobId(), userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", request.jobId()));

        Interview interview = Interview.builder()
                .job(job)
                .user(user)
                .type(request.type())
                .scheduledAt(request.scheduledAt())
                .durationMinutes(request.durationMinutes())
                .location(request.location())
                .interviewerName(request.interviewerName())
                .outcome(InterviewOutcome.PENDING)
                .reminderSent(false)
                .build();

        return  interviewMapper.toResponse(interviewRepository.save(interview));

    }

    @Transactional(readOnly = true)
    public Page<InterviewResponse> list(Pageable pageable) {

        UUID userId = currentUserProvider.getCurrentUserId();
        return interviewRepository.findByUserIdOrderByScheduledAtAsc(userId, pageable)
                .map(interviewMapper::toResponse);

    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> listUpcoming(int days) {

        UUID userId = currentUserProvider.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        return interviewRepository
                .findByUserIdAndScheduledAtBetweenOrderByScheduledAtAsc(userId, now, now.plusDays(days))
                .stream()
                .map(interviewMapper::toResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> listByJob(UUID jobId) {
        UUID userId = currentUserProvider.getCurrentUserId();
        jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", jobId));
        return interviewRepository.findByJobIdOrderByScheduledAtDesc(jobId)
                .stream()
                .map(interviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InterviewResponse getById(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Interview", id));
        return interviewMapper.toResponse(interview);
    }

    @Transactional
    public InterviewResponse update(UUID id, InterviewRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Interview", id));

        interview.setType(request.type());
        interview.setScheduledAt(request.scheduledAt());
        interview.setDurationMinutes(request.durationMinutes());
        interview.setLocation(request.location());
        interview.setInterviewerName(request.interviewerName());

        return interviewMapper.toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponse submitFeedback(UUID id, InterviewFeedbackRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Interview", id));

        interview.setFeedback(request.feedback());
        interview.setOutcome(request.outcome());

        return interviewMapper.toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public void delete(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Interview", id));
        interviewRepository.delete(interview);
    }

}
