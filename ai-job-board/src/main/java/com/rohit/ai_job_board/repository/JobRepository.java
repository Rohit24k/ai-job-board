package com.rohit.ai_job_board.repository;

import com.rohit.ai_job_board.dto.response.JobResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Jobs, Long> {

    List<Jobs> findByStatus(JobStatus status);

    Page<Jobs> findByStatus(JobStatus status, Pageable pageable);

//    Page<JobResponse> getAllJobs(String keyword, Pageable pageable);
    long countByRecruiterId(Long recruiterId);

    long countByRecruiterIdAndStatus(
            Long recruiterId,
            JobStatus status);
    @Query("""
            SELECT j
            FROM Jobs j
            WHERE
            j.status='OPEN'
            AND
            (
                :keyword IS NULL
                OR LOWER(j.title)
                    LIKE LOWER(CONCAT('%',:keyword,'%'))
                OR LOWER(j.location)
                    LIKE LOWER(CONCAT('%',:keyword,'%'))
                OR LOWER(j.requiredSkills)
                    LIKE LOWER(CONCAT('%',:keyword,'%'))
            )
            """)
    Page<Jobs> searchJobs( String keyword,  Pageable pageable);

    List<Jobs> findByRecruiter(User recruiter);

}