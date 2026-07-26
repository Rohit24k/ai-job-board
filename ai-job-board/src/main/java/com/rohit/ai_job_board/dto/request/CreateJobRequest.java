package com.rohit.ai_job_board.dto.request;

import com.rohit.ai_job_board.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String requiredSkills;

    @NotNull
    private Integer experience;

    @NotBlank
    private String location;

    @NotNull
    private Double salary;

    @NotNull
    private JobType jobType;
}