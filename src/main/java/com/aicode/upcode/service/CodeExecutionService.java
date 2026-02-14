package com.aicode.upcode.service;

import com.aicode.upcode.domain.Language;
import com.aicode.upcode.execution.CodeExecutor;
import com.aicode.upcode.execution.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final List<CodeExecutor> executors;

    public ExecutionResult execute(Language language,
                                   String code,
                                   String input,
                                   long timeLimitMs) {

        CodeExecutor executor = executors.stream()
                .filter(e -> e.supports(language))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Language not supported: " + language)
                );

        return executor.execute(language, code, input, timeLimitMs);
    }
}
