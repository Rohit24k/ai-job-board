package com.rohit.ai_job_board.service;


import com.rohit.ai_job_board.dto.response.InterviewQuestionResponse;

public interface InterviewService {

    InterviewQuestionResponse generateQuestions(Long jobId);

}
