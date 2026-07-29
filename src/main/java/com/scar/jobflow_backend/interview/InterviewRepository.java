package com.scar.jobflow_backend.interview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    Optional<Interview> findByIdAndUserId(UUID id, UUID userId);

    Page<Interview> findByUserIdOrderByScheduledAtAsc(UUID userId, Pageable pageable);

    List<Interview> findByJobIdOrderByScheduledAtDesc(UUID jobId);

    List<Interview> findByUserIdAndScheduledAtBetweenOrderByScheduledAtAsc(
            UUID userId, LocalDateTime start, LocalDateTime end
    );

}
