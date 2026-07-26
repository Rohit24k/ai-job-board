package com.rohit.ai_job_board.repository;

import com.rohit.ai_job_board.entity.Resume;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long> {

    List<Resume> findByJobIdOrderByMatchScoreDesc(Long jobId);
    List<Resume> findByCandidateIdOrderByUploadedAtDesc(Long candidateId);
    long countByJobRecruiterId(Long recruiterId);

    long countByJobRecruiterIdAndStatus(
            Long recruiterId,
            ApplicationStatus status);

    @Query("""
        SELECT AVG(r.matchScore)
         FROM Resume r
       WHERE r.job.recruiter.id = :recruiterId
        """)
    Double averageScore(Long recruiterId);

    List<Resume> findTop5ByJobRecruiterIdOrderByUploadedAtDesc(
            Long recruiterId);

}
