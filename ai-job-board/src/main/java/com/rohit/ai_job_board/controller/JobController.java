package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.request.CreateJobRequest;
import com.rohit.ai_job_board.dto.response.ApiResponse;
import com.rohit.ai_job_board.dto.response.JobResponse;
import com.rohit.ai_job_board.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

        private final JobService jobService;

        @PostMapping
        public ResponseEntity<JobResponse> createJob(
                        @Valid @RequestBody CreateJobRequest request) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(jobService.createJob(request));
        }

        @GetMapping
        public ResponseEntity<Page<JobResponse>> getJobs(
                        @RequestParam(required = false) String keyword,
                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                return ResponseEntity.ok(
                                jobService.getAllJobs(
                                                keyword,
                                                pageable));
        }

        @GetMapping("/{id}")
        public ResponseEntity<JobResponse> getJob(

                        @PathVariable Long id) {

                return ResponseEntity.ok(

                                jobService.getJob(id));

        }

        @PutMapping("/{id}")
        public ResponseEntity<JobResponse> updateJob(@PathVariable Long id,
                        @Valid @RequestBody CreateJobRequest request) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(jobService.updateJob(id, request));
        }

        @DeleteMapping("/{id}")
        public ApiResponse<?> deleteJob(@PathVariable Long id) {
                jobService.deleteJob(id);
                return new ApiResponse<>(id, "Deleted Success");
        }

        @PutMapping("/{id}/close")
        public ResponseEntity<JobResponse> closeJob(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                jobService.closeJob(id));

        }

        @PutMapping("/{id}/open")
        public ResponseEntity<JobResponse> reopenJob(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                jobService.reopenJob(id));

        }

}