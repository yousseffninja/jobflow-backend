package com.scar.jobflow_backend.company;

import com.scar.jobflow_backend.common.exception.ResourceNotFoundException;
import com.scar.jobflow_backend.common.storage.FileStorageService;
import com.scar.jobflow_backend.company.dto.CompanyRequest;
import com.scar.jobflow_backend.company.dto.CompanyResponse;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    @Transactional
    public CompanyResponse create(CompanyRequest request) {

        UUID userId = currentUserProvider.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        Company company = Company.builder()
                .user(user)
                .name(request.name())
                .logoUrl(request.logoUrl())
                .website(request.website())
                .hrContactName(request.hrContactName())
                .hrContactEmail(request.hrContactEmail())
                .build();

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponse(savedCompany);

    }

    public Page<CompanyResponse> list(String search, Pageable pageable) {
        UUID userId = currentUserProvider.getCurrentUserId();

        Page<Company> companies = (search == null || search.isBlank())
                ? companyRepository.findByUserId(userId, pageable)
                : companyRepository.findByUserIdAndNameContainingIgnoreCase(userId, search, pageable);

        return companies.map(companyMapper::toResponse);
    }

    public CompanyResponse getById(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Company company = companyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", id));
        return companyMapper.toResponse(company);
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Company company = companyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", id));

        company.setName(request.name());
        company.setLogoUrl(request.logoUrl());
        company.setWebsite(request.website());
        company.setHrContactName(request.hrContactName());
        company.setHrContactEmail(request.hrContactEmail());

        Company saved = companyRepository.save(company);
        return companyMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Company company = companyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", id));
        companyRepository.delete(company); // triggers @SQLDelete — soft delete
    }

    @Transactional
    public CompanyResponse uploadLogo(UUID id, org.springframework.web.multipart.MultipartFile file) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Company company = companyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", id));

        String logoUrl = fileStorageService.upload(file, "jobflow/company-logos");
        company.setLogoUrl(logoUrl);

        Company saved = companyRepository.save(company);
        return companyMapper.toResponse(saved);
    }

}
