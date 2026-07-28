package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecentApplicationDto;
import com.rohit.ai_job_board.dto.response.RecruiterDashboardResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.Resume;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import com.rohit.ai_job_board.enums.JobStatus;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.ApplicationRepository;
import com.rohit.ai_job_board.repository.JobRepository;
import com.rohit.ai_job_board.repository.ResumeRepository;
import com.rohit.ai_job_board.repository.UserRepository;
import com.rohit.ai_job_board.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final ResumeRepository resumeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List<CandidateApplicationResponse> getApplications(Long jobId) {

        // return applicationRepository
        //         .findByCandidateOrderByAppliedAtDesc(jobId)
        //         .stream()
        //         .map(r -> CandidateApplicationResponse.builder()
        //                 .resumeId(r.getId())
        //                 .candidateName(r.getCandidateName())
        //                 .matchScore(r.getMatchScore())
        //                 .summary(r.getSummary())
        //                 .uploadedAt(r.getUploadedAt())
        //                 .status(r.getStatus())
        //                 .build())
        //         .toList();
        return null;
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
    @Override
    public void updateApplicationStatus(
            Long applicationId,
            ApplicationStatus status) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

       User recruiter = userRepository.findByEmail(email)
               .orElseThrow(() -> new ResourceAlreadyExistsException("User Not Found"));

       
        Resume application =
                resumeRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceAlreadyExistsException(
                                        "Application not found"));
       if (!application.getJob()
               .getRecruiter()
               .getId()
               .equals(recruiter.getId())) {

           throw new AccessDeniedException(
                   "You are not allowed to update this application.");
       }

        application.setStatus(status);
        resumeRepository.save(application);



       application.setStatus(status);

       resumeRepository.save(application);

    }

    @Override
    public RecruiterDashboardResponse dashboard() {

        // Logged-in recruiter
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceAlreadyExistsException("Recruiter not found"));

        Long recruiterId = recruiter.getId();

        long totalJobs =
                jobRepository.countByRecruiterId(recruiterId);

        long activeJobs =
                jobRepository.countByRecruiterIdAndStatus(
                        recruiterId,
                        JobStatus.OPEN);

        long closedJobs =
                jobRepository.countByRecruiterIdAndStatus(
                        recruiterId,
                        JobStatus.CLOSED);

        long totalApplications =
                resumeRepository.countByJobRecruiterId(recruiterId);

        long shortlisted =
                resumeRepository.countByJobRecruiterIdAndStatus(
                        recruiterId,
                        ApplicationStatus.SHORTLISTED);

        long interview =
                resumeRepository.countByJobRecruiterIdAndStatus(
                        recruiterId,
                        ApplicationStatus.INTERVIEW);

        long rejected =
                resumeRepository.countByJobRecruiterIdAndStatus(
                        recruiterId,
                        ApplicationStatus.REJECTED);

        long hired =
                resumeRepository.countByJobRecruiterIdAndStatus(
                        recruiterId,
                        ApplicationStatus.HIRED);

        Double average =
                resumeRepository.averageScore(recruiterId);

        List<Resume> resumes =
                resumeRepository
                        .findTop5ByJobRecruiterIdOrderByUploadedAtDesc(
                                recruiterId);

        List<RecentApplicationDto> recent =
                resumes.stream()
                        .map(r -> RecentApplicationDto.builder()
                                .applicationId(r.getId())
                                .candidateName(r.getCandidate().getFirstName())
                                .jobTitle(r.getJob().getTitle())
                                .matchScore(r.getMatchScore())
                                .status(r.getStatus())
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

}
