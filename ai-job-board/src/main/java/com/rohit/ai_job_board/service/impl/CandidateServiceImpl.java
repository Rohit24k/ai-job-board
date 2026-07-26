package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.response.CandidateApplicationDto;
import com.rohit.ai_job_board.entity.User;
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

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    @Override
    public List<CandidateApplicationDto> myApplications() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User candidate =
                userRepository.findByEmail(email)
                        .orElseThrow();

        return resumeRepository
                .findByCandidateIdOrderByUploadedAtDesc(candidate.getId())
                .stream()
                .map(resume -> CandidateApplicationDto.builder()
                        .applicationId(resume.getId())
                        .jobTitle(resume.getJob().getTitle())
                        .matchScore(resume.getMatchScore())
                        .status(resume.getStatus())
                        .appliedOn(resume.getUploadedAt())
                        .build())
                .toList();

    }
}
