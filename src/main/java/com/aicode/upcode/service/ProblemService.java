package com.aicode.upcode.service;

import com.aicode.upcode.ai.AiService;
import com.aicode.upcode.domain.Problem;
import com.aicode.upcode.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final AiService aiService;

    public Problem generateProblem(String topic, String difficulty) {

        String description = aiService.generateProblem(topic, difficulty);

        Problem problem = Problem.builder()
                .title(topic + " Problem")
                .topic(topic)
                .difficulty(difficulty)
                .description(description)
                .build();

        return problemRepository.saveAndFlush(problem);
    }
}
