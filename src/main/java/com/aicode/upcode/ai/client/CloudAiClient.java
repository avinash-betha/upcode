package com.aicode.upcode.ai.client;

import com.aicode.upcode.ai.AiClient;
import org.springframework.stereotype.Component;

@Component
public class CloudAiClient implements AiClient {

    @Override
    public String generate(String prompt) {

        throw new UnsupportedOperationException(
                "Cloud AI not implemented yet."
        );
    }
}
