package com.aicode.upcode.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SubmissionResponse {

    private Long id;
    private String status;

    private long totalExecutionTime;
    private long totalMemoryUsedKb;

    private int totalScore;
    private int maxScore;
    private int passedCount;
    private int totalTestCases;

    private List<TestCaseResult> testCases;
}
