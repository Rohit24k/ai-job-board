package com.rohit.ai_job_board.dto.response;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    public ApiResponse(String message) {
        this.message = message;
    }
}
