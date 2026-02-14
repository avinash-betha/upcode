package com.aicode.upcode.controller;

import com.aicode.upcode.domain.Language;
import com.aicode.upcode.dto.SubmissionRequest;
import com.aicode.upcode.dto.SubmissionResponse;
import com.aicode.upcode.execution.ExecutionResult;
import com.aicode.upcode.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    // -----------------------------------
    // 🏆 Judge Mode (runs against DB test cases)
    // -----------------------------------
    @PostMapping("/judge")
    public SubmissionResponse judge(@RequestBody SubmissionRequest request) {

        return submissionService.submit(
                request.getProblemId(),
                request.getCode(),
                request.getLanguage()
        );
    }

    // -----------------------------------
    // 🧑‍💻 Playground Mode (custom input)
    // -----------------------------------
    @PostMapping("/run")
    public ExecutionResult run(@RequestBody SubmissionRequest request) {

        return submissionService.runCustom(
                request.getCode(),
                request.getLanguage(),
                request.getInput()
        );
    }
}
