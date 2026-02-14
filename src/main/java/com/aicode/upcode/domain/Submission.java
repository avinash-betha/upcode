package com.aicode.upcode.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long problemId;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(length = 10000)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String output;

    private String status; // pending, passed, failed

    private Long executionTime;

    private LocalDateTime createdAT;

    @PrePersist
    public void onCreate() {
        this.createdAT = LocalDateTime.now();
    }
}
