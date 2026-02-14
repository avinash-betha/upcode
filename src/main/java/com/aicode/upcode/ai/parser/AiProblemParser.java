package com.aicode.upcode.ai.parser;

import com.aicode.upcode.ai.dto.AiProblemResponse;

import java.util.ArrayList;
import java.util.List;

public class AiProblemParser {

    public static AiProblemResponse parse(String aiResponse) {

        String title = extract(aiResponse, "Title:", "Description:");
        String description = extract(aiResponse, "Description:", "Constraints:");
        String constraints = extract(aiResponse, "Constraints:", "Sample Input:");
        String sampleInput = extract(aiResponse, "Sample Input:", "Sample Output:");
        String sampleOutput = extract(aiResponse, "Sample Output:", "Explanation:");
        String explanation = extract(aiResponse, "Explanation:", "Reference Solution");
        String referenceSolution = extract(aiResponse,
                "Reference Solution (Java):",
                "Test Inputs:");

        return AiProblemResponse.builder()
                .title(clean(title))
                .description(clean(description))
                .constraints(clean(constraints))
                .sampleInput(clean(sampleInput))
                .sampleOutput(clean(sampleOutput))
                .explanation(clean(explanation))
                .referenceSolution(clean(referenceSolution))
                .build();
    }

    public static List<String> extractTestInputs(String aiResponse) {

        String section = extract(aiResponse, "Test Inputs:", null);

        List<String> inputs = new ArrayList<>();

        if (section != null) {
            String[] lines = section.split("\n");

            for (String line : lines) {
                if (!line.isBlank()) {
                    inputs.add(line.trim());
                }
            }
        }

        return inputs;
    }

    private static String extract(String text,
                                  String start,
                                  String end) {

        int startIndex = text.indexOf(start);

        if (startIndex == -1) return "";

        startIndex += start.length();

        if (end == null) {
            return text.substring(startIndex).trim();
        }

        int endIndex = text.indexOf(end, startIndex);

        if (endIndex == -1) {
            return text.substring(startIndex).trim();
        }

        return text.substring(startIndex, endIndex).trim();
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
