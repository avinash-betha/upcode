package com.aicode.upcode.service;

import com.aicode.upcode.ai.AiService;
import com.aicode.upcode.ai.dto.AiProblemResponse;
import com.aicode.upcode.ai.parser.AiProblemParser;
import com.aicode.upcode.domain.*;
import com.aicode.upcode.execution.ExecutionResult;
import com.aicode.upcode.repository.ProblemRepository;
import com.aicode.upcode.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiProblemCreationService {

    private final AiService aiService;
    private final CodeExecutionService executionService;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    public Problem generateAndPersist(String topic, String difficulty) {

        String raw = aiService.generateProblem(topic, difficulty);

        AiProblemResponse parsed =
                AiProblemParser.parse(raw);

        List<String> inputs =
                AiProblemParser.extractTestInputs(raw);

        // Save Problem
        Problem problem = Problem.builder()
                .title(parsed.getTitle())
                .description(parsed.getDescription())
                .difficulty(difficulty)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        // Generate expected outputs using reference solution
        for (String input : inputs) {

            ExecutionResult result =
                    executionService.execute(
                            Language.JAVA,
                            parsed.getReferenceSolution(),
                            input,
                            3000
                    );

            TestCase testCase = TestCase.builder()
                    .problemId(savedProblem.getId())
                    .input(input)
                    .expectedOutput(result.getOutput())
                    .hidden(false)
                    .weight(20)
                    .timeLimitMs(3000)
                    .build();

            testCaseRepository.save(testCase);
        }

        return savedProblem;
    }
}
