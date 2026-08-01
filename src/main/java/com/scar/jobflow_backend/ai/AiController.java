package com.scar.jobflow_backend.ai;

import com.scar.jobflow_backend.ai.AiService;
import com.scar.jobflow_backend.ai.dto.*;
import com.scar.jobflow_backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping(value = "/resume-review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AiGenerationResponse> reviewResume(
            @RequestParam UUID jobId,
            @RequestParam MultipartFile file
    ) {
        return ApiResponse.success(aiService.reviewResumeFromFile(jobId, file));
    }

    @PostMapping("/cover-letter")
    public ApiResponse<AiGenerationResponse> generateCoverLetter(@Valid @RequestBody CoverLetterRequest request) {
        return ApiResponse.success(aiService.generateCoverLetter(request));
    }

    @PostMapping("/interview-questions")
    public ApiResponse<AiGenerationResponse> generateInterviewQuestions(
            @Valid @RequestBody InterviewQuestionsRequest request
    ) {
        return ApiResponse.success(aiService.generateInterviewQuestions(request));
    }
}