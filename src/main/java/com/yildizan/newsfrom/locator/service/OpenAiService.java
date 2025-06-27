package com.yildizan.newsfrom.locator.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yildizan.newsfrom.locator.dto.OpenAiResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpenAiService {

    private final OpenAIClient openAIClient;
    
    @Value("${openai.deployment-name}")
    private String deploymentName;

    @Value("${openai.enabled}")
    private boolean enabled;
    
    public OpenAiService(@Value("${openai.api-key}") String apiKey, @Value("${openai.endpoint}") String endpoint) {
        this.openAIClient = new OpenAIClientBuilder()
            .credential(new KeyCredential(apiKey))
            .endpoint(endpoint)
            .buildClient();
    }

    public OpenAiResponseDto ask(String description) {
        if (!enabled) {
            return null;
        }

        ChatRequestSystemMessage instruction = new ChatRequestSystemMessage(
            "Where a news with following description should be shown on a world map? Provide a response with following format: {\"city\":<city name>,\"latitude\":<latitude>,\"longitude\":<longitude>}"
        );
        ChatRequestUserMessage message = new ChatRequestUserMessage(description);
        ChatCompletionsOptions options = new ChatCompletionsOptions(List.of(instruction, message));

        ChatCompletions completions;
        try {
            completions = openAIClient.getChatCompletions(deploymentName, options);
        } catch (HttpResponseException e) {
            log.warn("openai api error for text: " + description, e);
            return null;
        }

        CompletionsUsage usage = completions.getUsage();
        log.debug("Usage: " + usage.getPromptTokens() + " prompt tokens, " + usage.getCompletionTokens() + " completion tokens, " + usage.getTotalTokens() + " total tokens");

        String response = completions
            .getChoices()
            .get(0)
            .getMessage()
            .getContent();

        OpenAiResponseDto responseDto = null;
        try {
            responseDto = parseResponse(response);
        } catch (IOException e) {
            log.error("openai response parser error for response: " + response, e);
        }
        return responseDto;
    }

    private OpenAiResponseDto parseResponse(String response) throws IOException {
        return new ObjectMapper().readValue(response, new TypeReference<OpenAiResponseDto>() {});
    }

}
