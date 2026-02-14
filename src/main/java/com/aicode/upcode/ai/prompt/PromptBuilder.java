package com.aicode.upcode.ai.prompt;

public class PromptBuilder {

    public static String buildProblemPrompt(String topic,
                                            String difficulty) {

        return """
                Generate a coding problem in STRICT JSON format.

                Topic: %s
                Difficulty: %s

                Return ONLY valid JSON.
                No explanation outside JSON.

                Format:

                {
                  "title": "",
                  "description": "",
                  "constraints": "",
                  "sampleInput": "",
                  "sampleOutput": "",
                  "explanation": "",
                  "referenceSolution": "",
                  "testInputs": ["", "", "", "", ""]
                }

                Important:
                - Reference solution must be complete Java code.
                - testInputs must contain at least 5 diverse cases.
                - Do NOT include markdown.
                """.formatted(topic, difficulty);
    }

    public static String buildReviewPrompt(String problem,
                                           String code) {

        return """
                Analyze the following solution and return STRICT JSON only.

                {
                  "timeComplexity": "",
                  "spaceComplexity": "",
                  "optimization": "",
                  "cheatDetection": "",
                  "overallFeedback": ""
                }

                Problem:
                %s

                Code:
                %s
                """.formatted(problem, code);
    }
}
