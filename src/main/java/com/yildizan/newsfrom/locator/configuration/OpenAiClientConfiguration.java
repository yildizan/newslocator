package com.yildizan.newsfrom.locator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class OpenAiClientConfiguration {

    @Value("${openai.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor openAiApiKeyInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiKey);
    }

}
