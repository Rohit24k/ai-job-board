package com.rohit.ai_job_board.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public interface ResumeStorageService {

    String uploadResume(MultipartFile file) throws IOException;

    void deleteResume(String resumeKey) throws IOException;
    
}
