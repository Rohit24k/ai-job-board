package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.response.CandidateApplicationDto;
import com.rohit.ai_job_board.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/applications")
    public ResponseEntity<List<CandidateApplicationDto>> myApplications() {

        return ResponseEntity.ok(
                candidateService.myApplications());

    }

}
