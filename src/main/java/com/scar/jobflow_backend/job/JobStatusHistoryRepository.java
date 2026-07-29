package com.scar.jobflow_backend.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface JobStatusHistoryRepository extends JpaRepository<JobStatusHistory, UUID> {

    List<JobStatusHistory> findByJobIdOrderByChangedAtDesc(UUID jobId);
    @Query("""
            SELECT h FROM JobStatusHistory h
            WHERE h.job.user.id = :userId
            ORDER BY h.changedAt DESC
            """)
    List<JobStatusHistory> findRecentForUser(@Param("userId") UUID userId, Pageable pageable);

}