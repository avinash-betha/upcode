package com.aicode.upcode.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiProblemResponse {

    private String title;
    private String description;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;
    private String explanation;
    private String referenceSolution;
    private List<String> testInputs;
}

