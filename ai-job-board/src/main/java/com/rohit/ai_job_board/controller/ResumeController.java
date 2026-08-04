package com.rohit.ai_job_board.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rohit.ai_job_board.entity.Resume;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.ResumeRepository;
import com.rohit.ai_job_board.service.ResumeStorageService;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeStorageService storageService;

    private final ResumeRepository resumeRepository;
    private final Cloudinary cloudinary;

    // @GetMapping("/{resumeId}/file")
    // public ResponseEntity<Resource> getResumeFile(@PathVariable Long resumeId)
    // throws IOException {

    // Resume resume = resumeRepository.findById(resumeId)
    // .orElseThrow(() -> new ResourceAlreadyExistsException("Resume not found"));

    // String resumeKey = resume.getFilePath(); // resume/uuid.pdf

    // String publicId = resumeKey.substring(0, resumeKey.lastIndexOf('.'));
    // String extension = resumeKey.substring(resumeKey.lastIndexOf('.') + 1);

    // String url = cloudinary.url()
    // .resourceType("raw")
    // .secure(true)
    // .generate(publicId + "." + extension);
    // System.out.println( "URL " +url);
    // // Path path = Paths.get(resume.getFilePath());
    // // Resource resource = new UrlResource(path.toUri());
    // Resource resource = new UrlResource( url );
    // if (!resource.exists()) {
    // throw new RuntimeException("Resume file not found");
    // }

    // return ResponseEntity.ok()
    // .contentType(MediaType.APPLICATION_PDF)
    // .header(
    // HttpHeaders.CONTENT_DISPOSITION,
    // "inline; filename=\"" + resume.getFileName() + "\"")
    // .body(resource);
    // }

    @GetMapping("/{resumeId}/file")
    public ResponseEntity<Resource> getResumeFile(@PathVariable Long resumeId)
            throws IOException {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        URL cloudinaryUrl = new URL(resume.getFilePath());

        try (InputStream inputStream = cloudinaryUrl.openStream()) {

            byte[] bytes = inputStream.readAllBytes();

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resume.getFileName() + "\"")
                    .body(resource);
        }
    }

    @GetMapping("/{resumeId}/view")
    public ResponseEntity<String> getResumeUrl(@PathVariable Long resumeId) throws Exception {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Map result = cloudinary.api().resource(
                resume.getFilePath(),
                ObjectUtils.asMap(
                        "resource_type", "raw"));

        return ResponseEntity.ok((String) result.get("secure_url"));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile resume) throws IOException {
        System.out.println("In Upload");
        String key = storageService.uploadResume(resume);
        return ResponseEntity.ok(key);
    }

    // private final ResumeService resumeService;
    // @Autowired
    // private RecruiterService recruiterService;

    // // @PostMapping("/upload")
    // // public ResponseEntity<ResumeAnalysisResponse> uploadResume(
    // // @RequestParam MultipartFile resume,
    // // @RequestParam Long jobId) throws IOException {
    // // return ResponseEntity.ok(
    // // resumeService.uploadResume(
    // // resume,
    // // jobId));
    // //
    // // }

    // @GetMapping("/test")
    // public String test() {
    // return "OK";

    // }

    // @PostMapping(
    // value = "/upload",
    // consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    // )
    // @Operation(summary = "Upload resume")
    // public ResponseEntity<ResumeAnalysisResponse> uploadResume(
    // @Parameter(description = "Resume PDF")
    // @RequestParam("resume") MultipartFile resume,
    // @RequestParam("jobId") Long jobId) throws IOException {
    // System.out.println("Upload API Hit");
    // return ResponseEntity.ok(
    // resumeService.uploadResume(resume, jobId));
    // }

    // @PatchMapping("/applications/{applicationId}/status")
    // public ResponseEntity<String> updateStatus(

    // @PathVariable Long applicationId,

    // @RequestBody UpdateApplicationStatusRequest request) {

    // recruiterService.updateApplicationStatus(
    // applicationId,
    // request.getStatus());

    // return ResponseEntity.ok(
    // "Application status updated successfully");

    // }

}
