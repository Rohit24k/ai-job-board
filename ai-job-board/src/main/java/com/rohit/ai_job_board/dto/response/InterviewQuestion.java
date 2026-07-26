package com.rohit.ai_job_board.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    private String question;

    private String category;

    private String difficulty;

}