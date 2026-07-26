package com.rohit.ai_job_board.service;

import com.rohit.ai_job_board.dto.response.CandidateApplicationDto;

import java.util.List;

public interface CandidateService {

    List<CandidateApplicationDto> myApplications();

}