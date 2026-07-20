package com.scar.jobflow_backend.auth.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findTopByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, TokenType type);
}
