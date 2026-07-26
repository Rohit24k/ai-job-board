package com.rohit.ai_job_board.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohit.ai_job_board.dto.response.InterviewQuestionResponse;
import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;
    public String analyze(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        body.put("model", model);
        body.put("temperature", 0.2);
        body.put("max_tokens", 1000);

        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role", "user",
                        "content", prompt
                )
        );
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "https://api.groq.com/openai/v1/chat/completions",
                        request,
                        String.class);

        System.out.println("================================");
        System.out.println(response.getBody());
        System.out.println("================================");

        return response.getBody();
    }

    public ResumeAnalysisResponse analyzeResume(String prompt) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();

            body.put("model", model);

            body.put("messages", List.of(

                    Map.of(
                            "role", "system",
                            "content",
                            "You are an ATS Resume Analyzer. Always return ONLY valid JSON."
                    ),

                    Map.of(
                            "role", "user",
                            "content", prompt
                    )

            ));

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "https://api.groq.com/openai/v1/chat/completions",
                            request,
                            String.class);

            System.out.println("Body " +response.getBody());

            JsonNode root =
                    objectMapper.readTree(response.getBody());

//            String json =
//                    root.path("choices")
//                            .get(0)
//                            .path("message")
//                            .path("content")
//                            .asText();
            JsonNode choices = root.path("choices");

            if (choices.isEmpty()) {
                throw new RuntimeException("Groq returned no choices");
            }
            String json = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

            System.out.println("JSON " +json);
            json = cleanJson(json);

            if (!json.startsWith("{")) {
                throw new RuntimeException(
                        "AI did not return JSON.\n\nResponse:\n" + json
                );
            }

            return objectMapper.readValue(
                    json,
                    ResumeAnalysisResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to analyze resume",
                    e);

        }
    }

    public InterviewQuestionResponse generateInterviewQuestions(String prompt) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();

            body.put("model", model);
            body.put("temperature", 0.2);
            body.put("max_tokens", 1200);

            body.put("messages", List.of(
                    Map.of(
                            "role", "user",
                            "content", prompt
                    )
            ));

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "https://api.groq.com/openai/v1/chat/completions",
                            request,
                            String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            String json = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            json = cleanJson(json);

            return objectMapper.readValue(
                    json,
                    InterviewQuestionResponse.class);

        }
        catch (Exception e){

            throw new RuntimeException(
                    "Failed to generate interview questions",
                    e);

        }
    }


    private String cleanJson(String text){

        return text
                .replace("```json","")
                .replace("```","")
                .trim();

    }
}