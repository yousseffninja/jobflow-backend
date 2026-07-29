package com.scar.jobflow_backend.interview;

import com.scar.jobflow_backend.interview.dto.InterviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "companyName", source = "job.company.name")
    @Mapping(target = "companyLogoUrl", source = "job.company.logoUrl")
    InterviewResponse toResponse(Interview interview);

}
