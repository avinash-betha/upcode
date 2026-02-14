package com.aicode.upcode.ai;

import com.aicode.upcode.ai.dto.AiProblemResponse;
import com.aicode.upcode.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    // =====================================================
    // 1️⃣ Generate Raw Problem (Text)
    // =====================================================
    public String generateProblem(String topic, String difficulty) {

        String prompt = PromptBuilder.buildProblemPrompt(topic, difficulty);

        return aiClient.generate(prompt);
    }

    // =====================================================
    // 2️⃣ Generate + Parse JSON Problem (V1 Preferred)
    // =====================================================
    public AiProblemResponse generateAndParseProblem(String topic,
                                                     String difficulty) {

        try {

            String raw = generateProblem(topic, difficulty);

            // Clean possible markdown wrapping from AI
            raw = cleanJson(raw);

            return objectMapper.readValue(raw, AiProblemResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI JSON response", e);
        }
    }

    // =====================================================
    // 3️⃣ Code Review
    // =====================================================
    public String reviewCode(String problemDescription,
                             String userCode) {

        String prompt = PromptBuilder.buildReviewPrompt(
                problemDescription,
                userCode
        );

        return aiClient.generate(prompt);
    }

    // =====================================================
    // 4️⃣ Cheat Detection
    // =====================================================
    public String detectCheating(String problemDescription,
                                 String userCode) {

        String prompt = """
                Analyze the following solution for cheating patterns.

                Problem:
                %s

                Code:
                %s

                Detect:
                - Hardcoded outputs
                - Input echo tricks
                - Suspicious conditional branches
                - Non-generalized solutions
                - Pattern matching specific values

                Provide reasoning.
                """.formatted(problemDescription, userCode);

        return aiClient.generate(prompt);
    }

    // =====================================================
    // 5️⃣ Similarity Detection
    // =====================================================
    public String checkSimilarity(String codeA, String codeB) {

        String prompt = """
                Compare the following two code snippets.

                Code A:
                %s

                Code B:
                %s

                Are these implementations logically similar?
                Provide explanation.
                """.formatted(codeA, codeB);

        return aiClient.generate(prompt);
    }

    // =====================================================
    // Utility: Clean JSON if AI wraps with ```json
    // =====================================================
    private String cleanJson(String raw) {

        if (raw == null) return "";

        raw = raw.trim();

        if (raw.startsWith("```")) {
            raw = raw.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }

        return raw;
    }
}
