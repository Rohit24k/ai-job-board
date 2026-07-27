package com.rohit.ai_job_board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long applicationId;

    private Long resumeId;

    private Integer matchScore;

    private String status;

    private String message;

}
