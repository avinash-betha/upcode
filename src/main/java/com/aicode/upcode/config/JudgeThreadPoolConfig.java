package com.aicode.upcode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class JudgeThreadPoolConfig {

    @Bean
    public ExecutorService judgeExecutorService() {

        int cores = Runtime.getRuntime().availableProcessors();
        int threadCount = Math.max(2, cores - 1);

        return Executors.newFixedThreadPool(threadCount);
    }
}
