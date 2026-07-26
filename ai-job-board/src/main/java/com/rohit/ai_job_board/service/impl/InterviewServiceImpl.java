package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.response.InterviewQuestionResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.JobRepository;
import com.rohit.ai_job_board.service.GroqService;
import com.rohit.ai_job_board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final JobRepository jobRepository;

    private final GroqService groqService;

    @Override
    public InterviewQuestionResponse generateQuestions(Long jobId) {

        Jobs job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceAlreadyExistsException("Job not found"));

        String prompt = buildPrompt(job);

        return groqService.generateInterviewQuestions(prompt);
    }

    private String buildPrompt(Jobs job) {

        return """
        You are an experienced Technical Interviewer.

        Generate interview questions ONLY based on the following job.

        Return ONLY valid JSON.

        Do not return markdown.
        Do not explain anything.
        Do not wrap inside ```json.

        Generate exactly 10 questions.

        Difficulty should be:
        Easy
        Medium
        Hard

        Categories can be:
        Java
        Spring Boot
        SQL
        PostgreSQL
        REST API
        Microservices
        Docker
        Hibernate
        Security

        JSON Schema:

        {
          "questions":[
            {
              "question":"",
              "category":"",
              "difficulty":""
            }
          ]
        }

        Job Title:
        %s

        Description:
        %s

        Required Skills:
        %s

        """.formatted(
                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills()
        );
    }

}