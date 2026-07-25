package com.scar.jobflow_backend.company;

import com.scar.jobflow_backend.company.dto.CompanyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);
}