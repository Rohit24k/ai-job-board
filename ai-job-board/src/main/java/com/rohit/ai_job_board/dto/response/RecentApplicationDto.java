package com.rohit.ai_job_board.dto.response;

import com.rohit.ai_job_board.enums.ApplicationStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentApplicationDto {

    private Long applicationId;

    private String candidateName;

    private String jobTitle;

    private Integer matchScore;

    private ApplicationStatus status;

}
