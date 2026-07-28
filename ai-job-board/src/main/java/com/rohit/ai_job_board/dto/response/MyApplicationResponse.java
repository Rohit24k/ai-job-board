package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyApplicationResponse {

    private Long applicationId;

    private Long jobId;

    private String jobTitle;

    private String company;

    private Integer matchScore;

    private String status;

    private LocalDateTime appliedAt;

}
