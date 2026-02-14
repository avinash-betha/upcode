package com.aicode.upcode.service;

import com.aicode.upcode.domain.*;
import com.aicode.upcode.dto.SubmissionResponse;
import com.aicode.upcode.dto.TestCaseResult;
import com.aicode.upcode.execution.ExecutionResult;
import com.aicode.upcode.repository.SubmissionRepository;
import com.aicode.upcode.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final CodeExecutionService executionService;
    private final ExecutorService judgeExecutorService;

    // ======================================================
    // 🏆 JUDGE MODE
    // ======================================================
    public SubmissionResponse submit(Long problemId,
                                     String code,
                                     Language language) {

        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);

        if (testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for this problem.");
        }

        try {

            List<Callable<TestCaseExecutionWrapper>> tasks =
                    buildExecutionTasks(testCases, language, code);

            List<Future<TestCaseExecutionWrapper>> futures =
                    judgeExecutorService.invokeAll(tasks);

            return processResults(problemId, code, language, testCases, futures);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Parallel execution failed", e);
        }
    }

    // ======================================================
    // 🧑‍💻 PLAYGROUND MODE
    // ======================================================
    public ExecutionResult runCustom(String code,
                                     Language language,
                                     String input) {

        // default 5 sec timeout for playground
        long defaultTimeout = 5000;

        return executionService.execute(
                language,
                code,
                input,
                defaultTimeout
        );
    }

    // ======================================================
    // BUILD TASKS
    // ======================================================
    private List<Callable<TestCaseExecutionWrapper>> buildExecutionTasks(
            List<TestCase> testCases,
            Language language,
            String code) {

        List<Callable<TestCaseExecutionWrapper>> tasks = new ArrayList<>();

        for (TestCase testCase : testCases) {

            tasks.add(() -> {

                ExecutionResult result = executionService.execute(
                        language,
                        code,
                        testCase.getInput(),
                        testCase.getTimeLimitMs()
                );

                String actual = result.getOutput() == null
                        ? ""
                        : result.getOutput().trim();

                String expected = testCase.getExpectedOutput() == null
                        ? ""
                        : testCase.getExpectedOutput().trim();

                boolean passed = result.isSuccess()
                        && actual.equals(expected);

                return new TestCaseExecutionWrapper(testCase, result, passed);
            });
        }

        return tasks;
    }

    // ======================================================
    // PROCESS RESULTS
    // ======================================================
    private SubmissionResponse processResults(
            Long problemId,
            String code,
            Language language,
            List<TestCase> testCases,
            List<Future<TestCaseExecutionWrapper>> futures)
            throws InterruptedException, ExecutionException {

        boolean allPassed = true;

        long totalExecutionTime = 0;
        long totalMemoryUsed = 0;

        int totalScore = 0;
        int maxScore = 0;
        int passedCount = 0;

        List<TestCaseResult> visibleResults = new ArrayList<>();

        for (Future<TestCaseExecutionWrapper> future : futures) {

            TestCaseExecutionWrapper wrapper = future.get();

            TestCase testCase = wrapper.getTestCase();
            ExecutionResult result = wrapper.getResult();
            boolean passed = wrapper.isPassed();

            maxScore += testCase.getWeight();
            totalExecutionTime += result.getExecutionTime();
            totalMemoryUsed += result.getMemoryUsedKb();

            if (passed) {
                totalScore += testCase.getWeight();
                passedCount++;
            } else {
                allPassed = false;
            }

            if (!testCase.isHidden()) {
                visibleResults.add(
                        TestCaseResult.builder()
                                .input(testCase.getInput())
                                .expectedOutput(testCase.getExpectedOutput())
                                .actualOutput(result.getOutput())
                                .status(passed ? "PASSED" : "FAILED")
                                .executionTime(result.getExecutionTime())
                                .memoryUsedKb(result.getMemoryUsedKb())
                                .build()
                );
            }
        }

        Submission submission = Submission.builder()
                .problemId(problemId)
                .language(language)
                .code(code)
                .status(allPassed ? "PASSED" : "FAILED")
                .executionTime(totalExecutionTime)
                .output("Score: " + totalScore + "/" + maxScore)
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        return SubmissionResponse.builder()
                .id(savedSubmission.getId())
                .status(savedSubmission.getStatus())
                .totalExecutionTime(totalExecutionTime)
                .totalMemoryUsedKb(totalMemoryUsed)
                .totalScore(totalScore)
                .maxScore(maxScore)
                .passedCount(passedCount)
                .totalTestCases(testCases.size())
                .testCases(visibleResults)
                .build();
    }
}
