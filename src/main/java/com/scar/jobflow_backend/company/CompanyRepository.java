package com.scar.jobflow_backend.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Page<Company> findByUserId(UUID userId, Pageable pageable);

    Page<Company> findByUserIdAndNameContainingIgnoreCase(UUID userId, String name, Pageable pageable);

    Optional<Company> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}