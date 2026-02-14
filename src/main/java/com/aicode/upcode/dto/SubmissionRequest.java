package com.aicode.upcode.dto;

import com.aicode.upcode.domain.Language;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionRequest {

    private Long problemId;   // required for judge mode
    private String code;
    private Language language;

    // Only used for playground mode
    private String input;
}
