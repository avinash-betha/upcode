package com.aicode.upcode.ai.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiReviewResponse {

    private String timeComplexity;
    private String spaceComplexity;
    private String optimization;
    private String cheatDetection;
    private String overallFeedback;
}
