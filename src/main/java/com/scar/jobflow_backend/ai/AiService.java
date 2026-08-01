package com.scar.jobflow_backend.ai;

import com.scar.jobflow_backend.ai.dto.*;
import com.scar.jobflow_backend.common.exception.ResourceNotFoundException;
import com.scar.jobflow_backend.job.Job;
import com.scar.jobflow_backend.job.JobRepository;
import com.scar.jobflow_backend.security.CurrentUserProvider;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiProvider aiProvider;
    private final AiGenerationRepository aiGenerationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PdfTextExtractor pdfTextExtractor;

    @Transactional
    public AiGenerationResponse reviewResumeFromFile(UUID jobId, org.springframework.web.multipart.MultipartFile file) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = getOwnedJob(jobId, userId);

        String resumeText = pdfTextExtractor.extractText(file);

        String prompt = """
                You are an expert career coach and ATS (Applicant Tracking System) specialist.

                Analyze how well this resume matches the job description below.

                JOB TITLE: %s
                JOB DESCRIPTION: %s

                RESUME:
                %s

                Respond in this exact format:

                MATCH SCORE: [a number from 0-100]

                STRENGTHS:
                - [strength 1]
                - [strength 2]
                - [strength 3]

                GAPS:
                - [gap 1]
                - [gap 2]
                - [gap 3]

                Be honest and specific. Base the score on skills overlap, experience relevance, and keyword alignment.
                """.formatted(
                job.getTitle(),
                job.getDescription() != null ? job.getDescription() : "No description provided",
                resumeText
        );

        String result = aiProvider.generate(prompt);
        return save(userId, job, AiGenerationType.RESUME_REVIEW, resumeText, result);
    }
    
    @Transactional
    public AiGenerationResponse generateCoverLetter(CoverLetterRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = getOwnedJob(request.jobId(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        String tone = request.tone() != null ? request.tone() : "professional";

        String prompt = """
                Write a compelling, %s cover letter for the following job application.

                Applicant name: %s
                Job title: %s
                Company: %s
                Job description: %s

                Keep it concise (3-4 paragraphs), specific to the role, and avoid generic filler phrases.
                Do not include a header/date/address block — just the letter body.
                """.formatted(
                tone,
                user.getFullName(),
                job.getTitle(),
                job.getCompany().getName(),
                job.getDescription() != null ? job.getDescription() : "No description provided"
        );

        String result = aiProvider.generate(prompt);
        return save(userId, job, AiGenerationType.COVER_LETTER, null, result);
    }

    @Transactional
    public AiGenerationResponse generateInterviewQuestions(InterviewQuestionsRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Job job = getOwnedJob(request.jobId(), userId);

        String prompt = """
                Generate likely interview questions for this job, grouped into three categories.

                Job title: %s
                Company: %s
                Job description: %s

                Respond in this exact format:

                BEHAVIORAL:
                - [question 1]
                - [question 2]
                - [question 3]

                TECHNICAL:
                - [question 1]
                - [question 2]
                - [question 3]

                COMPANY-SPECIFIC:
                - [question 1]
                - [question 2]
                """.formatted(
                job.getTitle(),
                job.getCompany().getName(),
                job.getDescription() != null ? job.getDescription() : "No description provided"
        );

        String result = aiProvider.generate(prompt);
        return save(userId, job, AiGenerationType.INTERVIEW_QUESTIONS, null, result);
    }

    private Job getOwnedJob(UUID jobId, UUID userId) {
        return jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Job", jobId));
    }

    private AiGenerationResponse save(
            UUID userId, Job job, AiGenerationType type, String inputSnapshot, String result
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        AiGeneration generation = AiGeneration.builder()
                .user(user)
                .job(job)
                .type(type)
                .inputSnapshot(inputSnapshot)
                .result(result)
                .build();

        AiGeneration saved = aiGenerationRepository.save(generation);

        return new AiGenerationResponse(
                saved.getId(),
                saved.getType().name(),
                saved.getResult(),
                saved.getCreatedAt()
        );
    }
}