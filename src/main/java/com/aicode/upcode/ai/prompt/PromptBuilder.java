package com.aicode.upcode.ai.prompt;

public class PromptBuilder {

    // =====================================================
    // 🎯 STRICT JSON PROBLEM GENERATION
    // =====================================================
    public static String buildProblemPrompt(String topic,
                                            String difficulty) {

        return """
                You are a backend API that MUST return STRICT VALID JSON.

                RULES:
                1. Return ONLY ONE JSON object.
                2. Do NOT include markdown.
                3. Do NOT include explanations outside JSON.
                4. Do NOT include comments.
                5. Do NOT include trailing commas.
                6. Use double quotes ONLY.
                7. referenceSolution must be valid Java code inside a single string.
                8. testInputs must contain exactly 5 string values.
                9. Do NOT generate multiple problems.

                REQUIRED FORMAT:

                {
                  "title": "string",
                  "description": "string",
                  "constraints": "string",
                  "sampleInput": "string",
                  "sampleOutput": "string",
                  "explanation": "string",
                  "referenceSolution": "string",
                  "testInputs": ["string","string","string","string","string"]
                }

                Topic: %s
                Difficulty: %s

                Return ONLY the JSON object.
                """.formatted(topic, difficulty);
    }

    // =====================================================
    // 🎯 STRICT JSON CODE REVIEW
    // =====================================================
    public static String buildReviewPrompt(String problem,
                                           String code) {

        return """
                You are a backend API that returns STRICT VALID JSON.

                Return ONLY ONE JSON object.
                Do NOT include markdown.
                Do NOT include explanations outside JSON.
                Do NOT include comments.
                Do NOT include trailing commas.

                REQUIRED FORMAT:

                {
                  "timeComplexity": "string",
                  "spaceComplexity": "string",
                  "optimization": "string",
                  "cheatDetection": "string",
                  "overallFeedback": "string"
                }

                Problem:
                %s

                Code:
                %s

                Return ONLY JSON.
                """.formatted(problem, code);
    }
}
