package com.scar.jobflow_backend.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    Optional<Job> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT j FROM Job j
            WHERE j.user.id = :userId
            AND (:status IS NULL OR j.currentStatus = :status)
            AND (:priority IS NULL OR j.priority = :priority)
            AND (:companyId IS NULL OR j.company.id = :companyId)
            AND (:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Job> search(
            @Param("userId") UUID userId,
            @Param("status") JobStatus status,
            @Param("priority") Priority priority,
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            Pageable pageable
    );
}