package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.request.CreateJobRequest;
import com.rohit.ai_job_board.dto.response.JobResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.JobStatus;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.mapper.JobMapper;
import com.rohit.ai_job_board.repository.JobRepository;
import com.rohit.ai_job_board.repository.UserRepository;
import com.rohit.ai_job_board.service.JobService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

        private final JobRepository jobRepository;
        private final UserRepository userRepository;
        private final JobMapper jobMapper;

        @Override
        public JobResponse createJob(CreateJobRequest request) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                User recruiter = (User) authentication.getPrincipal();
                System.out.println("Recruiter " + recruiter.toString());
                Jobs job = jobMapper.toEntity(request);

                job.setRecruiter(recruiter);
                job.setStatus(JobStatus.OPEN);

                job = jobRepository.save(job);

                return jobMapper.toResponse(job);
        }

        // @Override
        // public Page<JobResponse> getAllJobs(
        // String keyword,
        // Pageable pageable){
        //
        // Page<Jobs> jobs =
        // jobRepository.searchJobs(keyword,pageable);
        //
        // return jobs.map(jobMapper::toResponse);
        // }

        @Override
        public Page<JobResponse> getAllJobs(String keyword, Pageable pageable) {

                Page<Jobs> jobs = jobRepository.findByStatus(JobStatus.OPEN, pageable);

                return jobs.map(jobMapper::toResponse);
        }

        @Override
        public JobResponse updateJob(
                        Long id,
                        CreateJobRequest request) {

                Jobs job = jobRepository.findById(id)

                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Job not found"));

                User recruiter = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                if (!job.getRecruiter().getId()
                                .equals(recruiter.getId())) {

                        throw new AccessDeniedException(
                                        "You cannot edit this job");

                }

                job.setTitle(request.getTitle());

                job.setDescription(request.getDescription());

                job.setRequiredSkills(request.getRequiredSkills());

                job.setExperience(request.getExperience());

                job.setLocation(request.getLocation());

                job.setSalary(request.getSalary());

                job.setJobType(request.getJobType());

                return jobMapper.toResponse(

                                jobRepository.save(job));

        }

        @Override
        public void deleteJob(Long id) {

                Jobs job = jobRepository.findById(id)

                                .orElseThrow(() -> new ResourceAlreadyExistsException(
                                                "Job not found"));

                User recruiter = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                if (!job.getRecruiter().getId()
                                .equals(recruiter.getId())) {

                        throw new AccessDeniedException(
                                        "Unauthorized");

                }

                jobRepository.delete(job);

        }

        @Override
        public JobResponse getJob(Long jobId) {

                // Jobs job = jobRepository.findById(jobId)
                //
                // .orElseThrow(()->
                // new ResourceAlreadyExistsException(
                // "Job not found"));
                Jobs job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                String loggedInEmail = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                if (!job.getRecruiter().getEmail().equals(loggedInEmail)) {
                        throw new AccessDeniedException("You are not authorized to view these applications");
                }

                return jobMapper.toResponse(job);

        }

        @Override
        public JobResponse closeJob(Long id) {

                Jobs job = jobRepository.findById(id)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                job.setStatus(JobStatus.CLOSED);

                jobRepository.save(job);

                return jobMapper.toResponse(job);

        }

        @Override
        public JobResponse reopenJob(Long id) {

                Jobs job = jobRepository.findById(id)
                                .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

                job.setStatus(JobStatus.OPEN);

                jobRepository.save(job);

                return jobMapper.toResponse(job);

        }

}
