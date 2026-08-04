package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.response.CandidateApplicationResponse;
import com.rohit.ai_job_board.dto.response.RecruiterDashboardResponse;
import com.rohit.ai_job_board.service.RecruiterService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<CandidateApplicationResponse>> getApplications(@PathVariable Long jobId) {

        return ResponseEntity.ok(
                recruiterService.getApplications(jobId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RecruiterDashboardResponse> dashboard() {

        return ResponseEntity.ok(
                recruiterService.dashboard());
    }

//     @GetMapping("/dashboard")
// public RecruiterDashboardResponse getDashboard(

//         @RequestParam(required = false)
//         Integer days) {

//     return recruiterService.getDashboard(days);


// }


}
