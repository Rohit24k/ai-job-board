package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantResponse {

    private Long applicationId;

    private Long candidateId;

    private String candidateName;

    private String email;

    private Integer matchScore;

    private String status;

    private LocalDateTime appliedAt;

}