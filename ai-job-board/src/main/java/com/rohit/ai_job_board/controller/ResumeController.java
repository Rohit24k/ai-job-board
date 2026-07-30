package com.rohit.ai_job_board.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.rohit.ai_job_board.entity.Resume;
import com.rohit.ai_job_board.exception.ResourceAlreadyExistsException;
import com.rohit.ai_job_board.repository.ResumeRepository;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

private final ResumeRepository resumeRepository;
@GetMapping("/{resumeId}/file")
public ResponseEntity<Resource> getResumeFile( @PathVariable Long resumeId ) throws IOException {

    Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new ResourceAlreadyExistsException("Resume not found"));

    Path path = Paths.get(resume.getFilePath());
    Resource resource = new UrlResource(path.toUri());
    if (!resource.exists()) {
        throw new RuntimeException("Resume file not found");
    }

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + resume.getFileName() + "\""
            )
            .body(resource);
}



//     private final ResumeService resumeService;
//     @Autowired
//     private RecruiterService recruiterService;

// //    @PostMapping("/upload")
// //    public ResponseEntity<ResumeAnalysisResponse> uploadResume(
// //            @RequestParam MultipartFile resume,
// //            @RequestParam Long jobId) throws IOException {
// //        return ResponseEntity.ok(
// //                resumeService.uploadResume(
// //                        resume,
// //                        jobId));
// //
// //    }


//         @GetMapping("/test")
//         public String test() {
//             return "OK";

//     }


//     @PostMapping(
//             value = "/upload",
//             consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//     )
//     @Operation(summary = "Upload resume")
//     public ResponseEntity<ResumeAnalysisResponse> uploadResume(
//             @Parameter(description = "Resume PDF")
//             @RequestParam("resume") MultipartFile resume,
//             @RequestParam("jobId") Long jobId) throws IOException {
//         System.out.println("Upload API Hit");
//         return ResponseEntity.ok(
//                 resumeService.uploadResume(resume, jobId));
//     }

//     @PatchMapping("/applications/{applicationId}/status")
//     public ResponseEntity<String> updateStatus(

//             @PathVariable Long applicationId,

//             @RequestBody UpdateApplicationStatusRequest request) {

//         recruiterService.updateApplicationStatus(
//                 applicationId,
//                 request.getStatus());

//         return ResponseEntity.ok(
//                 "Application status updated successfully");

//     }



}
