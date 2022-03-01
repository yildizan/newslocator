package com.yildizan.newsfrom.locator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class DiscordClientConfiguration {

    @Bean
    public HttpMessageConverter<Object> encoder() {
        return new MappingJackson2HttpMessageConverter();
    }

}
