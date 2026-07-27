package com.rohit.ai_job_board.repository;

import com.rohit.ai_job_board.entity.Application;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    Optional<Application> findByCandidateAndJob(User candidate,Jobs job);

    List<Application> findByCandidateOrderByAppliedAtDesc(User candidate);

    List<Application> findByJobOrderByAppliedAtDesc(Jobs job);

    List<Application> findByJob(Jobs job);

    Optional<Application> findByIdAndCandidate(Long id,User candidate);

}