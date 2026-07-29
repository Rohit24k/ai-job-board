package com.rohit.ai_job_board.dto.response;


import com.rohit.ai_job_board.enums.JobStatus;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterJobResponse {

    private Long jobId;

    private String title;

    private String location;

    private JobStatus status;

    private Integer totalApplications;

    private Double averageMatchScore;

    

}