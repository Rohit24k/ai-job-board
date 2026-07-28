package com.rohit.ai_job_board.repository;

import com.rohit.ai_job_board.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

}