package com.aicode.upcode.service;

import com.aicode.upcode.ai.AiService;
import com.aicode.upcode.ai.dto.AiProblemResponse;
import com.aicode.upcode.domain.*;
import com.aicode.upcode.execution.ExecutionResult;
import com.aicode.upcode.repository.ProblemRepository;
import com.aicode.upcode.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiProblemCreationService {

    private final AiService aiService;
    private final CodeExecutionService executionService;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    public Problem createProblem(String topic, String difficulty) {

        // 1️⃣ Generate structured problem from AI
        AiProblemResponse aiProblem =
                aiService.generateAndParseProblem(topic, difficulty);

        if (aiProblem.getReferenceSolution() == null ||
                aiProblem.getReferenceSolution().isBlank()) {
            throw new RuntimeException("AI did not return reference solution.");
        }

        if (aiProblem.getTestInputs() == null ||
                aiProblem.getTestInputs().isEmpty()) {
            throw new RuntimeException("AI did not return test inputs.");
        }

        // 2️⃣ Save Problem
        Problem problem = Problem.builder()
                .title(aiProblem.getTitle())
                .description(aiProblem.getDescription())
                .difficulty(difficulty)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        int weightPerTest =
                100 / aiProblem.getTestInputs().size();

        // 3️⃣ Generate expected outputs using reference solution
        for (int i = 0; i < aiProblem.getTestInputs().size(); i++) {

            String input = aiProblem.getTestInputs().get(i);

            ExecutionResult result =
                    executionService.execute(
                            Language.JAVA,
                            aiProblem.getReferenceSolution(),
                            input,
                            3000
                    );

            if (!result.isSuccess()) {
                throw new RuntimeException(
                        "Reference solution failed for input: " + input
                );
            }

            TestCase testCase = TestCase.builder()
                    .problemId(savedProblem.getId())
                    .input(input)
                    .expectedOutput(result.getOutput())
                    .hidden(i >= 2) // first 2 visible, rest hidden
                    .weight(weightPerTest)
                    .timeLimitMs(3000)
                    .build();

            testCaseRepository.save(testCase);
        }

        return savedProblem;
    }
}
