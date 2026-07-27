package com.rohit.ai_job_board.entity;

import com.rohit.ai_job_board.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tbl_applications",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "candidate_id",
                "job_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Jobs job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @CreationTimestamp
    private LocalDateTime appliedAt;

}