package com.rohit.ai_job_board.dto.response;

import com.rohit.ai_job_board.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateApplicationResponse {

    private Long resumeId;

    private String candidateName;

    private Integer matchScore;

    private String summary;

    private ApplicationStatus status;

    private LocalDateTime uploadedAt;
}