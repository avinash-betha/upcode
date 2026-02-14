package com.aicode.upcode.execution;

import com.aicode.upcode.domain.Language;

public interface CodeExecutor {

    boolean supports(Language language);

    ExecutionResult execute(Language language, String code, String input, long timeLimitMs);
}
