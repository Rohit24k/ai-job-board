package com.rohit.ai_job_board.dto.response;

import com.rohit.ai_job_board.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateApplicationDto {

    private Long applicationId;

    private String jobTitle;

    private Integer matchScore;

    private ApplicationStatus status;

    private LocalDateTime appliedOn;

}
