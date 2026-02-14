package com.aicode.upcode.controller;

import com.aicode.upcode.domain.Problem;
import com.aicode.upcode.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping("/generate")
    public Problem generateProblem(@RequestParam String topic, @RequestParam String difficulty) {
        return problemService.generateProblem(topic, difficulty);
    }
}
