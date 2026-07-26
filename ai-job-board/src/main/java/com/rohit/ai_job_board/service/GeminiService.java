package com.rohit.ai_job_board.service;

import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import com.rohit.ai_job_board.entity.Jobs;

public interface GeminiService {

    ResumeAnalysisResponse analyzeResume(
            Jobs job,
            String resumeText);

}
