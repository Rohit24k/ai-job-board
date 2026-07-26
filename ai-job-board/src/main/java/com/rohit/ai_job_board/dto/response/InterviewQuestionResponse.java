package com.rohit.ai_job_board.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionResponse {

    private List<InterviewQuestion> questions;

}