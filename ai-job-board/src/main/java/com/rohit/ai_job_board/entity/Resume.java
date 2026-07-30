package com.rohit.ai_job_board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String skillsFound;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    // @ManyToOne
    // @JoinColumn(name = "job_id")
    // private Jobs job;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private User candidate;

    private LocalDateTime uploadedAt;

    // @Enumerated(EnumType.STRING)
    // private ApplicationStatus status;

    private String filePath;

}
