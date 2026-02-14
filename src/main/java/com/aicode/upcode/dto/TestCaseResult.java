package com.aicode.upcode.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestCaseResult {

    private String input;
    private String expectedOutput;
    private String actualOutput;
    private String status;

    private long executionTime;
    private long memoryUsedKb;
}
