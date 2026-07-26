package com.rohit.ai_job_board.service.impl;

import com.rohit.ai_job_board.dto.request.LoginRequest;
import com.rohit.ai_job_board.dto.request.RegisterRequest;
import com.rohit.ai_job_board.dto.response.AuthenticationResponse;
import com.rohit.ai_job_board.dto.response.UserResponse;
import com.rohit.ai_job_board.entity.User;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.UserRepository;
import com.rohit.ai_job_board.security.JwtService;
import com.rohit.ai_job_board.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
        repository.save(user);
        String token = jwtService.generateToken(user);
        return buildResponse(user, token);

    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()

                )

        );

        User user = repository.findByEmail(request.getEmail())

                .orElseThrow(() ->

                        new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return buildResponse(user, token);

    }

    private AuthenticationResponse buildResponse(User user,
                                                 String token){

        return AuthenticationResponse.builder()

                .token(token)

                .type("Bearer")

                .expiresIn(86400L)

                .user(

                        UserResponse.builder()

                                .id(user.getId())

                                .firstName(user.getFirstName())

                                .lastName(user.getLastName())

                                .email(user.getEmail())

                                .role(user.getRole())

                                .build()

                )

                .build();

    }

}
