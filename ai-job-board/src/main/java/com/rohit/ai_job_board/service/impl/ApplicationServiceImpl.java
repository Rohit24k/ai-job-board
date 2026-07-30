package com.rohit.ai_job_board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.rohit.ai_job_board.dto.response.ApplicationAnalysisResponse;
import com.rohit.ai_job_board.dto.response.ApplicationResponse;
import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.JobAnalyticsResponse;
import com.rohit.ai_job_board.dto.response.MyApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecruiterDashboardResponse;
import com.rohit.ai_job_board.dto.response.RecruiterJobResponse;
import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import com.rohit.ai_job_board.entity.*;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.*;
import com.rohit.ai_job_board.service.ApplicationService;
import com.rohit.ai_job_board.service.GroqService;
import com.rohit.ai_job_board.util.PdfUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

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
                        Long jobId) throws IOException {

                User candidate = getLoggedInUser();

                Jobs job = getJob(jobId);

                validateApplication(candidate, job);

                String resumeText = PdfUtil.extractText(file.getBytes());

                ResumeAnalysisResponse analysis = groqService.analyzeResume(buildPrompt(job, resumeText));

                Resume resume = saveResume(file, resumeText, analysis, candidate);

                Application application = saveApplication(candidate, job, resume);

                return ApplicationResponse.builder()
                                .applicationId(application.getId())
                                .resumeId(resume.getId())
                                .matchScore(resume.getMatchScore())
                                .status(application.getStatus().name())
                                .message("Application submitted successfully.")
                                .build();

        }

        @Override
        public List<MyApplicationResponse> getMyApplications() {

                User candidate = getLoggedInUser();

                List<Application> applications = applicationRepository.findByCandidate(candidate);

                return applications.stream().map(this::mapToMyApplicationDto)
                                .collect(Collectors.toList());
        }

        @Override
        public ApplicationAnalysisResponse getApplicationAnalysis(Long applicationId) {

                String email = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                User recruiter = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Recruiter not found"));

                Application application = applicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Application not found"));

                if (!application.getJob()
                                .getRecruiter()
                                .getId()
                                .equals(recruiter.getId())) {

                        throw new AccessDeniedException(
                                        "You are not allowed to view this application.");
                }

                Resume resume = application.getResume();

                return ApplicationAnalysisResponse.builder()
                                .applicationId(application.getId())
                                .candidateName(resume.getCandidateName())
                                .matchScore(resume.getMatchScore())
                                .summary(resume.getSummary())
                                .strengths(toList(resume.getStrengths()))
                                .weaknesses(toList(resume.getWeaknesses()))
                                .skillsFound(toList(resume.getSkillsFound()))
                                .missingSkills(toList(resume.getMissingSkills()))
                                .suggestions(toList(resume.getSuggestions()))
                                .build();
        }

        @Override
        public ApplicationAnalysisResponse getRecruiterApplicationAnalysis(Long applicationId) {

                String email = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                User recruiter = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Recruiter not found"));

                Application application = applicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Application not found"));

                if (!application.getJob()
                                .getRecruiter()
                                .getId()
                                .equals(recruiter.getId())) {

                        throw new AccessDeniedException(
                                        "You are not allowed to view this application.");
                }

                Resume resume = application.getResume();

                return ApplicationAnalysisResponse.builder()
                                .applicationId(application.getId())
                                .candidateName(resume.getCandidateName())
                                .matchScore(resume.getMatchScore())
                                .summary(resume.getSummary())
                                .strengths(toList(resume.getStrengths()))
                                .weaknesses(toList(resume.getWeaknesses()))
                                .skillsFound(toList(resume.getSkillsFound()))
                                .missingSkills(toList(resume.getMissingSkills()))
                                .suggestions(toList(resume.getSuggestions()))
                                .build();
        }

        @Override
        public List<RecruiterJobResponse> getRecruiterJobs() {

                User recruiter = getLoggedInUser();

                List<Jobs> jobs = jobRepository.findByRecruiter(recruiter);

                return jobs.stream()

                                .map(job -> {

                                        List<Application> applications = applicationRepository.findByJob(job);

                                        return RecruiterJobResponse.builder()

                                                        .jobId(job.getId())

                                                        .title(job.getTitle())

                                                        .location(job.getLocation())
                                                        .status(job.getStatus())

                                                        .totalApplications(applications.size())

                                                        .averageMatchScore(calculateAverageScore(applications))

                                                        .build();

                                })

                                .toList();

        }

        @Override
        public List<CandidateApplicationResponse> getApplicants(Long jobId) {

                User recruiter = getLoggedInUser();

                Jobs job = getJob(jobId);

                validateRecruiterOwnership(job, recruiter);

                return applicationRepository

                                .findByJobOrderByAppliedAtDesc(job)

                                .stream()

                                .map(application ->

                                CandidateApplicationResponse.builder()

                                                .applicationId(application.getId())
                                                .resumeId(application.getResume().getId())
                                                .candidateName(application.getResume().getCandidateName())
                                                .email(application.getCandidate().getEmail())
                                                .matchScore(application.getResume().getMatchScore())
                                                .status(application.getStatus())
                                                .uploadedAt(application.getAppliedAt())
                                                .summary(application.getResume().getSummary())
                                                .strengths(application.getResume().getStrengths())
                                                .weaknesses(application.getResume().getWeaknesses())
                                                .skillsFound(application.getResume().getSkillsFound())
                                                .missingSkills(application.getResume().getMissingSkills())
                                                .suggestions(application.getResume().getSuggestions())

                                                .build()

                                )

                                .toList();

        }

        @Override
        public List<JobAnalyticsResponse> getJobAnalytics() {

                User recruiter = getLoggedInUser();
                List<Jobs> jobs = jobRepository.findByRecruiter(recruiter);
                return jobs.stream()

                                .map(job -> {

                                        List<Application> applications = applicationRepository.findByJob(job);

                                        double averageScore = applications.stream()

                                                        .map(Application::getResume)

                                                        .mapToInt(Resume::getMatchScore)

                                                        .average()

                                                        .orElse(0);

                                        return JobAnalyticsResponse.builder()

                                                        .jobId(job.getId())

                                                        .title(job.getTitle())

                                                        .totalApplications((long) applications.size())

                                                        .averageMatchScore(averageScore)

                                                        .build();

                                })

                                .toList();

        }

        @Override
        public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'updateApplicationStatus'");
        }

        private MyApplicationResponse mapToMyApplicationDto(Application application) {

                return MyApplicationResponse.builder()

                                .applicationId(application.getId())

                                .jobId(application.getJob().getId())

                                .jobTitle(application.getJob().getTitle())

                                // We'll improve this later once companyName exists
                                .company(application.getJob().getRecruiter().getFirstName() + " "
                                                + application.getJob().getRecruiter().getLastName())

                                .matchScore(application.getResume().getMatchScore())

                                .status(application.getStatus().name())

                                .appliedAt(application.getAppliedAt())

                                .build();

        }

        private List<String> toList(String json) {

                try {

                        return objectMapper.readValue(

                                        json,

                                        new TypeReference<List<String>>() {
                                        }

                        );

                } catch (Exception e) {

                        return List.of();

                }

        }

        private User getLoggedInUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                String email = authentication.getName();

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Candidate not found"));
        }

        private Jobs getJob(Long jobId) {

                return jobRepository
                                .findById(jobId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Job not found"));
        }

        private void validateApplication(
                        User candidate,
                        Jobs job) {

                if (applicationRepository
                                .findByCandidateAndJob(candidate, job)
                                .isPresent()) {

                        throw new ResourceAlreadyExistsException(
                                        "You have already applied for this job.");

                }

        }

        private void validateRecruiterOwnership(
                        Jobs job,
                        User recruiter) {

                if (!job.getRecruiter().getId().equals(recruiter.getId())) {

                        throw new ResourceAlreadyExistsException(
                                        "You are not allowed to access this job.");

                }

        }

        private Resume saveResume(MultipartFile file, String resumeText, ResumeAnalysisResponse response,
                        User candidate)
                        throws IOException {

                Path uploadDir = Paths.get("uploads", "resumes");
                Files.createDirectories(uploadDir);
                String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path savedFile = uploadDir.resolve(uniqueFileName);

                Files.copy(
                                file.getInputStream(),
                                savedFile,
                                StandardCopyOption.REPLACE_EXISTING);

                Resume resume = Resume.builder()
                                .candidateName(response.getCandidateName())
                                .fileName(file.getOriginalFilename())
                                .filePath(savedFile.toString())
                                .resumeText(resumeText)
                                .summary(response.getSummary())
                                .matchScore(response.getMatchScore())
                                .candidate(candidate)
                                .uploadedAt(java.time.LocalDateTime.now())
                                .build();

                resume.setStrengths(objectMapper.writeValueAsString(response.getStrengths()));

                resume.setWeaknesses(objectMapper.writeValueAsString(response.getWeaknesses()));

                resume.setSkillsFound(objectMapper.writeValueAsString(response.getSkillsFound()));

                resume.setMissingSkills(objectMapper.writeValueAsString(response.getMissingSkills()));

                resume.setSuggestions(objectMapper.writeValueAsString(response.getSuggestions()));

                return resumeRepository.save(resume);

        }

        private double calculateAverageScore(List<Application> applications) {

                return applications.stream()

                                .map(Application::getResume)

                                .mapToInt(Resume::getMatchScore)

                                .average()

                                .orElse(0);

        }

        private Application saveApplication(User candidate, Jobs job, Resume resume) {

                Application application = Application.builder()
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
                                resume);
        }

        @Override
        public Page<CandidateApplicationResponse> getApplications(Long jobId, Pageable pageable) {

                Jobs job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                Page<Application> applications = applicationRepository.findByJob(job, pageable);
                return applications.map(application ->

                CandidateApplicationResponse.builder()

                                .applicationId(application.getId())
                                .resumeId(application.getResume().getId())

                                .candidateName(
                                                application.getCandidate().getFirstName()
                                                                + " "
                                                                + application.getCandidate().getLastName())

                                .email(application.getCandidate().getEmail())

                                .matchScore(application.getResume().getMatchScore())

                                .summary(application.getResume().getSummary())

                                .strengths(application.getResume().getStrengths())

                                .weaknesses(application.getResume().getWeaknesses())

                                .skillsFound(application.getResume().getSkillsFound())

                                .missingSkills(application.getResume().getMissingSkills())

                                .suggestions(application.getResume().getSuggestions())

                                .status(application.getStatus())

                                .uploadedAt(application.getAppliedAt())

                                .build()

                );
        }

}