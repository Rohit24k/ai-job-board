package com.rohit.ai_job_board.controller;

import com.rohit.ai_job_board.dto.request.LoginRequest;
import com.rohit.ai_job_board.dto.request.RegisterRequest;
import com.rohit.ai_job_board.dto.response.AuthenticationResponse;
import com.rohit.ai_job_board.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register( @Valid @RequestBody RegisterRequest request ) {

        return ResponseEntity.ok( authenticationService.register(request) );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login( @Valid @RequestBody LoginRequest request ) {

        System.out.println( "Request " + request );
        System.out.println( "Response " + authenticationService.login(request) );

        return ResponseEntity.ok( authenticationService.login(request) );
    }

}
