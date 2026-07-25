package com.scar.jobflow_backend.job;

import com.scar.jobflow_backend.job.dto.JobResponse;
import com.scar.jobflow_backend.job.dto.JobStatusHistoryResponse;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyLogoUrl", source = "company.logoUrl")
    JobResponse toResponse(Job job);

    JobStatusHistoryResponse toHistoryResponse(JobStatusHistory history);
}