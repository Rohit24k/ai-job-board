package com.rohit.ai_job_board.service;

import com.rohit.ai_job_board.dto.request.CreateJobRequest;
import com.rohit.ai_job_board.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface JobService {

    JobResponse createJob(CreateJobRequest request);

    Page<JobResponse> getAllJobs( String keyword, Pageable pageable);

    JobResponse updateJob(Long jobId, CreateJobRequest request);

    void deleteJob(Long jobId);

//    List<JobResponse> getAllJobs();

    JobResponse getJob(Long jobId);

    JobResponse closeJob(Long id);

    JobResponse reopenJob(Long id);
}
