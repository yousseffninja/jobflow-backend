package com.scar.jobflow_backend.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String logoUrl,
        String website,
        String hrContactName,
        String hrContactEmail,
        LocalDateTime createdAt
) {}