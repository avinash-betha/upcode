package com.aicode.upcode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiConfig {

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
