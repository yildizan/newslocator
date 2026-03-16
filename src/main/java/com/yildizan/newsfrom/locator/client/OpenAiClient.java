package com.yildizan.newsfrom.locator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yildizan.newsfrom.locator.configuration.OpenAiClientConfiguration;
import com.yildizan.newsfrom.locator.dto.openai.ChatRequestDto;
import com.yildizan.newsfrom.locator.dto.openai.ChatResponseDto;

@FeignClient(name = "openAiClient", url = "${openai.endpoint}", configuration = OpenAiClientConfiguration.class)
public interface OpenAiClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    ChatResponseDto getChatCompletions(@RequestBody ChatRequestDto request);

}
