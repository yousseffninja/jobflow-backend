package com.scar.jobflow_backend.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiGenerationRepository extends JpaRepository<AiGeneration, UUID> {

    List<AiGeneration> findByUserIdAndJobIdAndTypeOrderByCreatedAtDesc(
            UUID userId, UUID jobId, AiGenerationType type
    );
}
