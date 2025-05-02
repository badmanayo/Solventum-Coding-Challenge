package com.example.shortlink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class ConcurrencyConfig {

    @Value("${concurrency.limit}")
    private int limit;

    @Bean
    public Semaphore semaphore() {
        return new Semaphore(limit);
    }
}
