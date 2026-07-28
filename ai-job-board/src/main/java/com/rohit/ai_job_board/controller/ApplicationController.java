package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.response.ApplicationResponse;
import com.rohit.ai_job_board.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(
            value = "/apply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApplicationResponse> apply(

            @RequestParam MultipartFile file,

            @RequestParam Long jobId

    ) throws IOException {

        return ResponseEntity.ok(

                applicationService.apply(

                        file,

                        jobId

                )

        );

    }

}