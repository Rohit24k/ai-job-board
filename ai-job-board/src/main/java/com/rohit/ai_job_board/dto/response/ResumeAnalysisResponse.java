package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisResponse {

    private String candidateName;

    private Integer matchScore;

    private String summary;

    private List<String> skillsFound;

    private List<String> missingSkills;

    private List<String> strengths;

    private List<String> weaknesses;

    private List<String> suggestions;

}
