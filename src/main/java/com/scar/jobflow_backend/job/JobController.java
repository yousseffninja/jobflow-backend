package com.scar.jobflow_backend.job;

import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.job.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> search(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(jobService.search(status, priority, companyId, search, pageable))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Job updated successfully", jobService.update(id, request))
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody JobStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Job status updated successfully", jobService.updateStatus(id, request))
        );
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApiResponse<List<JobStatusHistoryResponse>>> getStatusHistory(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getStatusHistory(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        jobService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully", null));
    }
}