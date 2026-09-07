package com.scar.jobflow_backend.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(

        @NotBlank(message = "Company name is required")
        String name,

        String logoUrl,
        String website,
        String hrContactName,

        @Email(message = "HR contact email must be valid")
        String hrContactEmail
) {}