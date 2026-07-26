package com.rohit.ai_job_board.dto.response;

import com.rohit.ai_job_board.enums.JobStatus;
import com.rohit.ai_job_board.enums.JobType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;

    private String title;

    private String description;

    private String requiredSkills;

    private Integer experience;

    private String location;

    private Double salary;

    private JobType jobType;

    private JobStatus status;

    private String recruiterName;

    private LocalDateTime createdAt;
}
