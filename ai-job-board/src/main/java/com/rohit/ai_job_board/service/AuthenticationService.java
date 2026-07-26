package com.rohit.ai_job_board.service;


import com.rohit.ai_job_board.dto.request.LoginRequest;
import com.rohit.ai_job_board.dto.request.RegisterRequest;
import com.rohit.ai_job_board.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);

}