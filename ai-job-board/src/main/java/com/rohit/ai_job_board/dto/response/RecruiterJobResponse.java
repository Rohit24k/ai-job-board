package com.rohit.ai_job_board.dto.response;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterJobResponse {

    private Long jobId;

    private String title;

    private String location;

    private Integer totalApplications;

    private Double averageMatchScore;

}