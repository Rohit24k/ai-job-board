package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.response.CandidateApplicationDto;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.enums.ApplicationStatus;
import com.rohit.ai_job_board.repository.ApplicationRepository;
import com.rohit.ai_job_board.repository.ResumeRepository;
import com.rohit.ai_job_board.repository.UserRepository;
import com.rohit.ai_job_board.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final ApplicationRepository applicationRepository;

    private final UserRepository userRepository;

    @Override
public List<CandidateApplicationDto> myApplications() {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    User candidate = userRepository
            .findByEmail(email)
            .orElseThrow();

    return applicationRepository
            .findByCandidateOrderByAppliedAtDesc(candidate)
            .stream()
            .map(application -> CandidateApplicationDto.builder()

                    .applicationId(application.getId())

                    .jobTitle(application.getJob().getTitle())

                    .matchScore(
                            application.getResume().getMatchScore())

                    .status(application.getStatus())

                    .appliedOn(application.getAppliedAt())

                    .build())

            .toList();

}

}
