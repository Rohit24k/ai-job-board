package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.request.UpdateApplicationStatusRequest;
import com.rohit.ai_job_board.dto.response.ApplicationAnalysisResponse;
import com.rohit.ai_job_board.dto.response.ApplicationResponse;
import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.JobAnalyticsResponse;
import com.rohit.ai_job_board.dto.response.MyApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecruiterJobResponse;
import com.rohit.ai_job_board.service.ApplicationService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

        private final ApplicationService applicationService;

        @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApplicationResponse> apply( @RequestParam MultipartFile file, 
                                                          @RequestParam Long jobId) throws IOException {
                return ResponseEntity.ok(applicationService.apply(file, jobId));
        }

        @GetMapping("/my-applications")
        public ResponseEntity<List<MyApplicationResponse>> getMyApplications() {

                return ResponseEntity.ok(applicationService.getMyApplications());

        }

        @GetMapping("/{applicationId}/analysis")
        public ResponseEntity<ApplicationAnalysisResponse> getAnalysis(@PathVariable Long applicationId) {

                return ResponseEntity.ok(
                                applicationService.getApplicationAnalysis(applicationId));

        }

        // @GetMapping("/job/{jobId}/applications")
        // public ResponseEntity<List<CandidateApplicationResponse>> getApplicants(
        // @PathVariable Long jobId ) {
        // return ResponseEntity.ok( applicationService.getApplicants(jobId) );
        // }

        @GetMapping("/job/{jobId}/applications")
        public ResponseEntity<Page<CandidateApplicationResponse>> getApplications( @PathVariable Long jobId,
             @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable ) {
               return ResponseEntity.ok( applicationService.getApplications( jobId, pageable) );
        }

        @GetMapping("/recruiter/jobs")
        public ResponseEntity<List<RecruiterJobResponse>> getRecruiterJobs() {
                return ResponseEntity.ok(applicationService.getRecruiterJobs());

        }

        @GetMapping("/recruiter/{applicationId}/analysis")
        public ResponseEntity<ApplicationAnalysisResponse> getRecruiterAnalysis(@PathVariable Long applicationId) {
                return ResponseEntity.ok(applicationService.getRecruiterApplicationAnalysis(applicationId));
        }

        @PutMapping("/{applicationId}/status")
        public ResponseEntity<Void> updateStatus(
                        @PathVariable Long applicationId, @RequestBody UpdateApplicationStatusRequest request) {
                applicationService.updateApplicationStatus(applicationId, request.getStatus());

                return ResponseEntity.ok().build();
        }

        @GetMapping("/recruiter/job-analytics")
        public ResponseEntity<List<JobAnalyticsResponse>> getJobAnalytics() {

                return ResponseEntity.ok(
                                applicationService.getJobAnalytics());

        }

}