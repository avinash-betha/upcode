package com.aicode.upcode.ai.client;

import com.aicode.upcode.ai.AiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Component
@Primary   // Local AI is default
@RequiredArgsConstructor
public class LocalAiClient implements AiClient {

    private final RestTemplate restTemplate;

    @Override
    public String generate(String prompt) {

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-coder",
                "prompt", prompt,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        return response.getBody().get("response").toString();
    }
}
