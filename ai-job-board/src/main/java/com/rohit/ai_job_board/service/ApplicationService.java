package com.rohit.ai_job_board.service;

import com.rohit.ai_job_board.dto.response.*;
import com.rohit.ai_job_board.enums.ApplicationStatus;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ApplicationService {

    ApplicationResponse apply(MultipartFile file, Long jobId) throws IOException;

    List<MyApplicationResponse> getMyApplications();

    ApplicationAnalysisResponse getApplicationAnalysis(Long applicationId );

    List<RecruiterJobResponse> getRecruiterJobs();

    List<ApplicantResponse> getApplicants(Long jobId);

    ApplicationAnalysisResponse getRecruiterApplicationAnalysis(Long applicationId);

    void updateApplicationStatus( Long applicationId, ApplicationStatus status);

}