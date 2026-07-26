package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.request.UpdateApplicationStatusRequest;
import com.rohit.ai_job_board.dto.response.ResumeAnalysisResponse;
import com.rohit.ai_job_board.service.RecruiterService;
import com.rohit.ai_job_board.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import java.io.IOException;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    @Autowired
    private RecruiterService recruiterService;

//    @PostMapping("/upload")
//    public ResponseEntity<ResumeAnalysisResponse> uploadResume(
//            @RequestParam MultipartFile resume,
//            @RequestParam Long jobId) throws IOException {
//        return ResponseEntity.ok(
//                resumeService.uploadResume(
//                        resume,
//                        jobId));
//
//    }


        @GetMapping("/test")
        public String test() {
            return "OK";

    }


    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Upload resume")
    public ResponseEntity<ResumeAnalysisResponse> uploadResume(
            @Parameter(description = "Resume PDF")
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobId") Long jobId) throws IOException {
        System.out.println("Upload API Hit");
        return ResponseEntity.ok(
                resumeService.uploadResume(resume, jobId));
    }

    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<String> updateStatus(

            @PathVariable Long applicationId,

            @RequestBody UpdateApplicationStatusRequest request) {

        recruiterService.updateApplicationStatus(
                applicationId,
                request.getStatus());

        return ResponseEntity.ok(
                "Application status updated successfully");

    }

}
