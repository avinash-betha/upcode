package com.aicode.upcode.service;

import com.aicode.upcode.domain.TestCase;
import com.aicode.upcode.execution.ExecutionResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestCaseExecutionWrapper {

    private TestCase testCase;
    private ExecutionResult result;
    private boolean passed;
}
