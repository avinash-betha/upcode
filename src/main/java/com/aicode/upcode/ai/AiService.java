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
    // 1️⃣ Generate Raw Problem (Text Only)
    // =====================================================
    public String generateProblem(String topic, String difficulty) {

        String prompt = PromptBuilder.buildProblemPrompt(topic, difficulty);

        return aiClient.generate(prompt);
    }

    // =====================================================
    // 2️⃣ Generate + Parse Problem Into Structured DTO
    // =====================================================
    public AiProblemResponse generateAndParseProblem(String topic,
                                                     String difficulty) {

        try {

            String rawResponse = generateProblem(topic, difficulty);

            System.out.println("========== RAW AI RESPONSE ==========");
            System.out.println(rawResponse);
            System.out.println("=====================================");

            // Extract only JSON block
            String json = extractJson(rawResponse);

            // Sanitize malformed JSON
            json = sanitizeJson(json);

            return objectMapper.readValue(json, AiProblemResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI JSON response", e);
        }
    }

    // =====================================================
    // 3️⃣ Code Review (Approach Analysis)
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
    // 5️⃣ Code Similarity Check
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
    // 🔐 JSON EXTRACTION (Important for LLM Stability)
    // =====================================================
    private String extractJson(String raw) {

        if (raw == null) return "";

        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");

        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }

        return raw;
    }

    // =====================================================
    // 🛡 JSON SANITIZER (Fix common LLM formatting issues)
    // =====================================================
    private String sanitizeJson(String raw) {

        if (raw == null) return "";

        // Remove markdown formatting
        raw = raw.replace("```json", "")
                .replace("```", "");

        // Remove bullet dashes inside arrays
        raw = raw.replace("\n-", "\n");

        // Remove trailing commas
        raw = raw.replaceAll(",\\s*}", "}")
                .replaceAll(",\\s*]", "]");

        return raw.trim();
    }
}
