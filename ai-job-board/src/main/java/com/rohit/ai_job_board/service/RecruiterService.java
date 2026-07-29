package com.rohit.ai_job_board.service;

import com.rohit.ai_job_board.dto.response.ApplicantResponse;
import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecruiterDashboardResponse;
import com.rohit.ai_job_board.enums.ApplicationStatus;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface RecruiterService {

    List<CandidateApplicationResponse> getApplications(Long jobId);

    void updateApplicationStatus(Long applicationId, ApplicationStatus status);
    List<ApplicantResponse> getApplicants(Long jobId);

    RecruiterDashboardResponse dashboard();

    // RecruiterDashboardResponse getDashboard(Integer days);

    
}
