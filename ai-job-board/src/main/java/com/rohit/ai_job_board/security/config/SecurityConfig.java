package com.rohit.ai_job_board.security.config;

import com.rohit.ai_job_board.security.handler.JwtAuthenticationEntryPoint;
import com.rohit.ai_job_board.security.jwt.JwtAuthenticationFilter;
import com.rohit.ai_job_board.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final CustomUserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;
        private final JwtAuthenticationEntryPoint authenticationEntryPoint;

        @Bean
        public AuthenticationProvider authenticationProvider() {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

                // provider.setUserDetailsService(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authenticationEntryPoint))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/jobs/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/applications/apply")
                                                .hasRole("CANDIDATE")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/applications/my-applications")
                                                .hasRole("CANDIDATE")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/applications/job/*/applications")
                                                .hasRole("RECRUITER")
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/applications/*/status")
                                                .hasRole("RECRUITER")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/applications/recruiter/jobs")
                                                .hasRole("RECRUITER")
                                                .anyRequest()
                                                .authenticated())

                                // .requestMatchers(
                                // "/api/auth/**",
                                // "/swagger-ui/**",
                                // "/v3/api-docs/**",
                                // "/api/resumes/**"
                                // ).permitAll()
                                // .requestMatchers(HttpMethod.POST, "/api/jobs/**") // because of this only
                                // RECRUITERS will be able to post the Job
                                // .hasRole("RECRUITER")
                                // .anyRequest()
                                // .authenticated()
                                // )

                                .authenticationProvider(authenticationProvider())

                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

}