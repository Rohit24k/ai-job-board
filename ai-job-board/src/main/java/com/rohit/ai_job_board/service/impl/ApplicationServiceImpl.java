package com.rohit.ai_job_board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohit.ai_job_board.dto.response.ApplicationResponse;
import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import com.rohit.ai_job_board.entity.*;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.*;
import com.rohit.ai_job_board.service.ApplicationService;
import com.rohit.ai_job_board.service.GroqService;
import com.rohit.ai_job_board.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final JobRepository jobRepository;

    private final UserRepository userRepository;

    private final ResumeRepository resumeRepository;

    private final ApplicationRepository applicationRepository;

    private final GroqService groqService;

    private final ObjectMapper objectMapper;

@Override
public ApplicationResponse apply(
        MultipartFile file,
        Long jobId
) throws IOException {

    User candidate = getLoggedInCandidate();

    Jobs job = getJob(jobId);

    validateApplication(candidate, job);

    String resumeText = PdfUtil.extractText(file.getBytes());

    ResumeAnalysisResponse analysis =
            groqService.analyzeResume(
                    buildPrompt(job, resumeText)
            );

   Resume resume = saveResume(
        file,
        resumeText,
        analysis,
        candidate
);

    Application application =
            saveApplication(
                    candidate,
                    job,
                    resume
            );

    return ApplicationResponse.builder()
            .applicationId(application.getId())
            .resumeId(resume.getId())
            .matchScore(resume.getMatchScore())
            .status(application.getStatus().name())
            .message("Application submitted successfully.")
            .build();

}


private User getLoggedInCandidate() {

    Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String email = authentication.getName();

    return userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new ResourceAlreadyExistsException(
                            "Candidate not found"
                    ));
}

private Jobs getJob(Long jobId) {

    return jobRepository
            .findById(jobId)
            .orElseThrow(() ->
                    new ResourceAlreadyExistsException(
                            "Job not found"
                    ));
}

private void validateApplication(
        User candidate,
        Jobs job
) {

    if (applicationRepository
            .findByCandidateAndJob(candidate, job)
            .isPresent()) {

        throw new ResourceAlreadyExistsException(
                "You have already applied for this job."
        );

    }

}



private Resume saveResume(
        MultipartFile file,
        String resumeText,
        ResumeAnalysisResponse response,
        User candidate
) throws IOException {

    Resume resume = Resume.builder()

            .candidateName(response.getCandidateName())

            .fileName(file.getOriginalFilename())

            .resumeText(resumeText)

            .summary(response.getSummary())

            .matchScore(response.getMatchScore())

            .candidate(candidate)

            .uploadedAt(java.time.LocalDateTime.now())

            .build();

    resume.setStrengths(
            objectMapper.writeValueAsString(
                    response.getStrengths()));

    resume.setWeaknesses(
            objectMapper.writeValueAsString(
                    response.getWeaknesses()));

    resume.setSkillsFound(
            objectMapper.writeValueAsString(
                    response.getSkillsFound()));

    resume.setMissingSkills(
            objectMapper.writeValueAsString(
                    response.getMissingSkills()));

    resume.setSuggestions(
            objectMapper.writeValueAsString(
                    response.getSuggestions()));

    return resumeRepository.save(resume);

}

private Application saveApplication(
        User candidate,
        Jobs job,
        Resume resume
) {

    Application application =
            Application.builder()

                    .candidate(candidate)

                    .job(job)

                    .resume(resume)

                    .status(ApplicationStatus.APPLIED)

                    .build();

    return applicationRepository.save(application);

}


private String buildPrompt(Jobs job, String resume) {

        return """
You are an ATS Resume Analyzer.

Your task is to analyze the provided resume against the provided job description.

You MUST perform the analysis.

DO NOT explain the task.
DO NOT describe the schema.
DO NOT answer with markdown.
DO NOT use ```json.
DO NOT wrap the response.
DO NOT invent candidate information.

Return ONLY a valid JSON object.

Rules:
- candidateName must be extracted from the resume.
- matchScore must be an integer between 0 and 100.
- summary should be less than 80 words.
- skillsFound should contain only skills present in both resume and job.
- missingSkills should contain required skills missing from the resume.
- strengths should contain 3-5 points.
- weaknesses should contain 3-5 points.
- suggestions should contain 5 concrete improvements.

JSON Schema:

{
  "candidateName":"",
  "matchScore":0,
  "summary":"",
  "skillsFound":[],
  "missingSkills":[],
  "strengths":[],
  "weaknesses":[],
  "suggestions":[]
}

JOB TITLE:
%s

JOB DESCRIPTION:
%s

REQUIRED SKILLS:
%s

RESUME:
%s
""".formatted(
                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills(),
                resume
        );
    }



}