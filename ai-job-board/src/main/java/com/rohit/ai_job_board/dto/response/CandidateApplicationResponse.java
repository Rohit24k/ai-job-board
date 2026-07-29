package com.rohit.ai_job_board.dto.response;

import com.rohit.ai_job_board.enums.ApplicationStatus;
import lombok.*;
import java.time.LocalDateTime;


@Builder
@Getter
@Setter
public class CandidateApplicationResponse {

    private Long applicationId;

    private Long resumeId;

    private String candidateName;

    private String email;

    private Integer matchScore;

    private String summary;

    private String strengths;

    private String weaknesses;

    private String skillsFound;

    private String missingSkills;

    private String suggestions;

    private ApplicationStatus status;

    private LocalDateTime uploadedAt;

}