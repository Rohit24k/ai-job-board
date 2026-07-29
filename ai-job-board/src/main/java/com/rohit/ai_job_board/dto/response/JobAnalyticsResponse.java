package com.rohit.ai_job_board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalyticsResponse {

    private Long jobId;

    private String title;

    private Long totalApplications;

    private Double averageMatchScore;

}