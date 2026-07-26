package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.response.InterviewQuestionResponse;
import com.rohit.ai_job_board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/generate/{jobId}")
    public ResponseEntity<InterviewQuestionResponse>
    generateQuestions(
            @PathVariable Long jobId){
        return ResponseEntity.ok(
                interviewService.generateQuestions(jobId));
    }

}
