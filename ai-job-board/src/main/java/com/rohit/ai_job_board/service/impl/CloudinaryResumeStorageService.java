package com.rohit.ai_job_board.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rohit.ai_job_board.service.ResumeStorageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryResumeStorageService implements ResumeStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadResume(MultipartFile file) throws IOException {
        validate(file);
        String extension = getExtension(file.getOriginalFilename());
        String publicId = "resume/" + UUID.randomUUID();

        File tempFile = File.createTempFile("resume-", "." + extension);
         String secure_url ="";
        try {
            file.transferTo(tempFile);
            Map<?, ?> result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "public_id", publicId,
                            "overwrite", false));
                            secure_url = (String) result.get("secure_url");
            System.out.println(result);
        } finally {
            tempFile.delete();
        }
        return secure_url;
    }

    @Override
    public void deleteResume(String resumeKey) throws IOException {

        String publicId = removeExtension(resumeKey);

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "raw"));
    }

    private void validate(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Resume cannot be empty.");
        }

        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("Invalid filename.");
        }

        String ext = getExtension(filename);

        if (!ext.equalsIgnoreCase("pdf")
                && !ext.equalsIgnoreCase("doc")
                && !ext.equalsIgnoreCase("docx")) {

            throw new IllegalArgumentException(
                    "Only PDF, DOC and DOCX are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Maximum file size is 5MB.");
        }
    }

    private String getExtension(String filename) {

        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String removeExtension(String filename) {

        return filename.substring(0, filename.lastIndexOf('.'));
    }
}
