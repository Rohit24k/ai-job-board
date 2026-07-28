package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAnalysisResponse {

    private Long applicationId;

    private String candidateName;

    private Integer matchScore;

    private String summary;

    private List<String> strengths;

    private List<String> weaknesses;

    private List<String> skillsFound;

    private List<String> missingSkills;

    private List<String> suggestions;

}
