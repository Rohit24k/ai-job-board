package com.rohit.ai_job_board.repository;

import com.rohit.ai_job_board.entity.Application;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.ApplicationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByCandidateAndJob(User candidate, Jobs job);

    List<Application> findByCandidate(User candidate);

    List<Application> findByCandidateOrderByAppliedAtDesc(User candidate);

    List<Application> findByJobOrderByAppliedAtDesc(Jobs job);
    
    long countByJob(Jobs job);

    List<Application> findByJob(Jobs job);

    Optional<Application> findByIdAndCandidate(Long id, User candidate);

    long countByJobRecruiterId(Long recruiterId);

    long countByJobRecruiterIdAndStatus( Long recruiterId, ApplicationStatus status);

      List<Application> findTop5ByJobRecruiterIdOrderByAppliedAtDesc( Long recruiterId);

        @Query(" SELECT AVG(a.resume.matchScore) FROM Application a WHERE a.job.recruiter.id = :recruiterId ")
        Double averageScore(Long recruiterId);

}