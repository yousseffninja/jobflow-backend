package com.scar.jobflow_backend.interview;


import com.scar.jobflow_backend.common.response.ApiResponse;
import com.scar.jobflow_backend.interview.dto.InterviewFeedbackRequest;
import com.scar.jobflow_backend.interview.dto.InterviewRequest;
import com.scar.jobflow_backend.interview.dto.InterviewResponse;
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
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewResponse>> create(@Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview scheduled successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InterviewResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.list(pageable)));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> listUpcoming(
            @RequestParam(defaultValue = "14") int days
    ) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.listUpcoming(days)));
    }

    @GetMapping("/by-job/{jobId}")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> listByJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.listByJob(jobId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody InterviewRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Interview updated successfully", interviewService.update(id, request))
        );
    }

    @PatchMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<InterviewResponse>> submitFeedback(
            @PathVariable UUID id, @Valid @RequestBody InterviewFeedbackRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Feedback submitted successfully", interviewService.submitFeedback(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        interviewService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Interview deleted successfully", null));
    }
}
