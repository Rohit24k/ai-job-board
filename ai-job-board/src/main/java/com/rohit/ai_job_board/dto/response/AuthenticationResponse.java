package com.rohit.ai_job_board.dto.response;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;

    private String type;

    private Long expiresIn;

    private UserResponse user;
}