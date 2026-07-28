package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.response.ApplicantResponse;
import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecentApplicationDto;
import com.rohit.ai_job_board.dto.response.RecruiterDashboardResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import com.rohit.ai_job_board.enums.JobStatus;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.ApplicationRepository;
import com.rohit.ai_job_board.repository.JobRepository;
import com.rohit.ai_job_board.repository.UserRepository;
import com.rohit.ai_job_board.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.rohit.ai_job_board.entity.Application;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private JobRepository jobRepository;
        @Autowired
        private ApplicationRepository applicationRepository;

        @Override
        public List<ApplicantResponse> getApplicants(Long jobId) {

                Jobs job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                return applicationRepository
                                .findByJobOrderByAppliedAtDesc(job)
                                .stream()
                                .map(application -> ApplicantResponse.builder()
                                                .applicationId(application.getId())
                                                .candidateId(application.getCandidate().getId())
                                                .candidateName(application.getCandidate().getFirstName() + " "
                                                                + application.getCandidate().getLastName())
                                                .email(application.getCandidate().getEmail())
                                                .matchScore(application.getResume().getMatchScore())
                                                .status(application.getStatus().toString())
                                                .appliedAt(application.getAppliedAt())
                                                .build())
                                .toList();
        }

        private User getLoggedInCandidate() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                String email = authentication.getName();

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Candidate not found"));
        }

        @Override
        public void updateApplicationStatus(
                        Long applicationId,
                        ApplicationStatus status) {

                Application application = applicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Application not found"));

                application.setStatus(status);

                applicationRepository.save(application);
        }

        @Override
        public RecruiterDashboardResponse dashboard() {

                // Logged-in recruiter
                String email = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                User recruiter = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Recruiter not found"));

                Long recruiterId = recruiter.getId();

                long totalJobs = jobRepository.countByRecruiterId(recruiterId);

                long activeJobs = jobRepository.countByRecruiterIdAndStatus(
                                recruiterId,
                                JobStatus.OPEN);

                long closedJobs = jobRepository.countByRecruiterIdAndStatus(
                                recruiterId,
                                JobStatus.CLOSED);

                long totalApplications = applicationRepository.countByJobRecruiterId(recruiterId);

                long shortlisted = applicationRepository.countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.SHORTLISTED);

                long interview = applicationRepository.countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.INTERVIEW);

                long rejected = applicationRepository.countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.REJECTED);

                long hired = applicationRepository.countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.HIRED);

                Double average = applicationRepository.averageScore(recruiterId);

                List<Application> applications = applicationRepository.findTop5ByJobRecruiterIdOrderByAppliedAtDesc(recruiterId);

                List<RecentApplicationDto> recent = applications.stream()
                                .map(a -> RecentApplicationDto.builder()
                                                .applicationId(a.getId())
                                                .candidateName(
                                                                a.getCandidate().getFirstName()
                                                                                + " "
                                                                                + a.getCandidate().getLastName())
                                                .jobTitle(a.getJob().getTitle())
                                                .matchScore(a.getResume().getMatchScore())
                                                .status(a.getStatus())
                                                .build())
                                .toList();

                return RecruiterDashboardResponse.builder()
                                .totalJobs(totalJobs)
                                .activeJobs(activeJobs)
                                .closedJobs(closedJobs)
                                .totalApplications(totalApplications)
                                .shortlisted(shortlisted)
                                .interview(interview)
                                .rejected(rejected)
                                .hired(hired)
                                .averageMatchScore(
                                                average == null ? 0.0 : average)
                                .recentApplications(recent)
                                .build();
        }

        @Override
        public List<CandidateApplicationResponse> getApplications(Long jobId) {

                Jobs job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                return applicationRepository
                                .findByJobOrderByAppliedAtDesc(job)
                                .stream()
                                .map(application -> CandidateApplicationResponse.builder()
                                                .resumeId(application.getResume().getId())
                                                .candidateName(
                                                                application.getCandidate().getFirstName()
                                                                                + " "
                                                                                + application.getCandidate()
                                                                                                .getLastName())
                                                .matchScore(application.getResume().getMatchScore())
                                                .summary(application.getResume().getSummary())
                                                .status(application.getStatus())
                                                .uploadedAt(application.getAppliedAt())
                                                .build())
                                .toList();
        }

}
