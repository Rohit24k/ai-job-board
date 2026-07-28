// package com.rohit.ai_job_board.service.impl;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
// import com.rohit.ai_job_board.entity.Jobs;
// import com.rohit.ai_job_board.entity.Resume;
// import com.rohit.ai_job_board.entity.User;
// import com.rohit.ai_job_board.enums.ApplicationStatus;
// import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
// import com.rohit.ai_job_board.repository.JobRepository;
// import com.rohit.ai_job_board.repository.ResumeRepository;
// import com.rohit.ai_job_board.repository.UserRepository;
// import com.rohit.ai_job_board.service.GeminiService;
// import com.rohit.ai_job_board.service.GroqService;
// import com.rohit.ai_job_board.service.ResumeService;
// import com.rohit.ai_job_board.util.PdfUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.time.LocalDateTime;

// @Service
// public class ResumeServiceImpl implements ResumeService {

//     @Autowired
//     private JobRepository jobRepository;
//     @Autowired
//     private GroqService groqService;
//     @Autowired
//     private ResumeRepository resumeRepository;
//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public ResumeAnalysisResponse uploadResume(
//             MultipartFile file,
//             Long jobId) throws IOException {

// //        String resumeText =
// //                PdfUtil.extractText(
// //                        file.getBytes());
// //
// //        System.out.println(resumeText);
// //
// //        return ResumeAnalysisResponse.builder()
// //
// //                .summary(resumeText)
// //
// //                .matchScore(0)
// //
// //                .build();

//         String resumeText = PdfUtil.extractText(file.getBytes());
// //         Can be used for verifying is user registered or not
//         Authentication authentication =
//                 SecurityContextHolder.getContext().getAuthentication();
//         String email = authentication.getName();

//         User candidate = userRepository
//                 .findByEmail(email)
//                 .orElseThrow(() ->
//                         new ResourceAlreadyExistsException("User not found"));
//         Jobs job = jobRepository.findById(jobId)
//                 .orElseThrow(() -> new ResourceAlreadyExistsException("Job not found"));

// //        return groqService.analyzeResume(buildPrompt(job, resumeText));
//         ResumeAnalysisResponse response =
//                 groqService.analyzeResume(buildPrompt(job, resumeText));
//         Resume resumeEntity = Resume.builder()
//                 .candidateName(response.getCandidateName())
//                 .fileName(file.getOriginalFilename())
//                 .resumeText(resumeText)
//                 .summary(response.getSummary())
//                 .matchScore(response.getMatchScore())
//                 .uploadedAt(LocalDateTime.now())
//                 .job(job)
//                 .build();
//         resumeEntity.setStatus(ApplicationStatus.APPLIED);
//         ObjectMapper mapper = new ObjectMapper();
//        resumeEntity.setCandidate(candidate);
//         resumeEntity.setStrengths(
//                 mapper.writeValueAsString(response.getStrengths()));

//         resumeEntity.setWeaknesses(
//                 mapper.writeValueAsString(response.getWeaknesses()));

//         resumeEntity.setSkillsFound(
//                 mapper.writeValueAsString(response.getSkillsFound()));

//         resumeEntity.setMissingSkills(
//                 mapper.writeValueAsString(response.getMissingSkills()));

//         resumeEntity.setSuggestions(
//                 mapper.writeValueAsString(response.getSuggestions()));
//         resumeRepository.save(resumeEntity);
//         return response;
//     }



// //    private String buildPrompt(Jobs job, String resume) {
// //
// //        return """
// //                candidateName
// //                                                    - Read from resume only.
// //
// //                                                    matchScore
// //                                                    - Integer between 0 and 100.
// //
// //                                                    summary
// //                                                    - Maximum 80 words.
// //
// //                                                    skillsFound
// //                                                    - Skills present in BOTH resume and job.
// //
// //                                                    missingSkills
// //                                                    - Required skills missing from resume.
// //
// //                                                    strengths
// //                                                    - 3 to 5 strengths.
// //
// //                                                    weaknesses
// //                                                    - 3 to 5 weaknesses.
// //
// //                                                    suggestions
// //                                                    - 5 concrete improvements.
// //""".formatted(
// //                job.getTitle(),
// //                job.getDescription(),
// //                job.getRequiredSkills(),
// //                resume
// //        );
// //
// //    }

//     private String buildPrompt(Jobs job, String resume) {

//         return """
// You are an ATS Resume Analyzer.

// Your task is to analyze the provided resume against the provided job description.

// You MUST perform the analysis.

// DO NOT explain the task.
// DO NOT describe the schema.
// DO NOT answer with markdown.
// DO NOT use ```json.
// DO NOT wrap the response.
// DO NOT invent candidate information.

// Return ONLY a valid JSON object.

// Rules:
// - candidateName must be extracted from the resume.
// - matchScore must be an integer between 0 and 100.
// - summary should be less than 80 words.
// - skillsFound should contain only skills present in both resume and job.
// - missingSkills should contain required skills missing from the resume.
// - strengths should contain 3-5 points.
// - weaknesses should contain 3-5 points.
// - suggestions should contain 5 concrete improvements.

// JSON Schema:

// {
//   "candidateName":"",
//   "matchScore":0,
//   "summary":"",
//   "skillsFound":[],
//   "missingSkills":[],
//   "strengths":[],
//   "weaknesses":[],
//   "suggestions":[]
// }

// JOB TITLE:
// %s

// JOB DESCRIPTION:
// %s

// REQUIRED SKILLS:
// %s

// RESUME:
// %s
// """.formatted(
//                 job.getTitle(),
//                 job.getDescription(),
//                 job.getRequiredSkills(),
//                 resume
//         );
//     }

// }
