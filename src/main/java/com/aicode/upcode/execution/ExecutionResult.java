package com.aicode.upcode.execution;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExecutionResult {

    private boolean success;        // true if execution succeeded
    private String output;          // program output or error message
    private long executionTime;     // execution time in milliseconds
    private long memoryUsedKb;      // approximate memory usage in KB
}
