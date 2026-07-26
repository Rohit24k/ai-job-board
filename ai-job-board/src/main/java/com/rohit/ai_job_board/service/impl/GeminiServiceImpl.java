package com.rohit.ai_job_board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohit.ai_job_board.dto.request.GeminiRequest;
import com.rohit.ai_job_board.dto.response.GeminiResponse;
import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import com.rohit.ai_job_board.entity.Jobs;
import com.rohit.ai_job_board.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public ResumeAnalysisResponse analyzeResume(
            Jobs job,
            String resumeText) {

        try {

            String prompt =
                    buildPrompt(job, resumeText);

            GeminiRequest request =
                    new GeminiRequest(
                            List.of(
                                    new GeminiRequest.Content(
                                            List.of(
                                                    new GeminiRequest.Part(prompt)
                                            )
                                    )
                            )
                    );

            System.out.println(objectMapper.writeValueAsString(request));
            GeminiResponse response =
                    restClient.post()

                            .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                            .retrieve()

                            .body(GeminiResponse.class);

            String aiJson =
                    response.getCandidates()
                            .get(0)
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();

            aiJson = cleanJson(aiJson);

            return objectMapper.readValue(
                    aiJson,
                    ResumeAnalysisResponse.class);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to analyze resume",
                    e);

        }

    }

    private String buildPrompt(Jobs job, String resume) {

        return """
You are an experienced technical recruiter.

Compare this resume with the job.

Job Title:
%s

Job Description:
%s

Required Skills:
%s

Resume:
%s

Return ONLY valid JSON.

{
  "candidateName":"",
  "matchScore":0,
  "summary":"",
  "skillsFound":[],
  "missingSkills":[],
  "strengths":[],
  "weaknesses":[],
  "suggestions":[]
}
""".formatted(

                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills(),
                resume
        );

    }

    private String cleanJson(String text){

        return text
                .replace("```json","")
                .replace("```","")
                .trim();

    }
}