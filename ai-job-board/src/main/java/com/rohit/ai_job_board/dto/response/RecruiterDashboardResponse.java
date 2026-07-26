package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardResponse {

    private Long totalJobs;

    private Long activeJobs;

    private Long closedJobs;

    private Long totalApplications;

    private Long shortlisted;

    private Long interview;

    private Long rejected;

    private Long hired;

    private Double averageMatchScore;

    private List<RecentApplicationDto> recentApplications;

}