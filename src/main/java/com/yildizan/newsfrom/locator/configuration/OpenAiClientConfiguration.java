package com.yildizan.newsfrom.locator.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;

@Configuration
public class OpenAiClientConfiguration {

    @Value("${openai.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor openAiApiKeyInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiKey);
    }

    @Bean
    public Request.Options openAiRequestOptions() {
        return new Request.Options(5, TimeUnit.SECONDS, 120, TimeUnit.SECONDS, true);
    }

    @Bean
    public Retryer openAiRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }

}
