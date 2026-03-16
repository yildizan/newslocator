package com.yildizan.newsfrom.locator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yildizan.newsfrom.locator.client.OpenAiClient;
import com.yildizan.newsfrom.locator.dto.openai.ChatRequestDto;
import com.yildizan.newsfrom.locator.dto.openai.ChatResponseDto;
import com.yildizan.newsfrom.locator.dto.openai.ChatMessageDto;
import com.yildizan.newsfrom.locator.dto.openai.LocateRequestDto;
import com.yildizan.newsfrom.locator.dto.openai.LocateResponseDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final int MAX_BATCH_SIZE = 750;
    private static final String SYSTEM_PROMPT = """
            You are a geolocation assistant. You will receive a JSON array of news items, each with "id", "title", and "description". \
            For each news item, determine the most relevant location on a world map where the news should be shown. \
            Respond with a JSON array containing objects with "id", "lat", "lon", and "place" fields. \
            Example input: [{"id":1,"title":"...","description":"..."}] \
            Example output: [{"id":1,"lat":41.01,"lon":28.97,"place":"Istanbul"}] \
            Respond ONLY with the JSON array, no additional text.""";

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.enabled}")
    private boolean enabled;

    public List<LocateResponseDto> askBatch(List<LocateRequestDto> items) {
        if (!enabled || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<LocateResponseDto> allResults = new ArrayList<>();

        for (int i = 0; i < items.size(); i += MAX_BATCH_SIZE) {
            List<LocateRequestDto> chunk = items.subList(i, Math.min(i + MAX_BATCH_SIZE, items.size()));
            List<LocateResponseDto> chunkResults = askChunk(chunk);
            allResults.addAll(chunkResults);
        }

        return allResults;
    }

    private List<LocateResponseDto> askChunk(List<LocateRequestDto> chunk) {
        String userContent;
        try {
            userContent = objectMapper.writeValueAsString(chunk);
        } catch (IOException e) {
            log.error("failed to serialize request items", e);
            return Collections.emptyList();
        }

        ChatMessageDto systemMessage = new ChatMessageDto("system", SYSTEM_PROMPT);
        ChatMessageDto userMessage = new ChatMessageDto("user", userContent);
        ChatRequestDto request = new ChatRequestDto(List.of(systemMessage, userMessage));

        ChatResponseDto completions;
        try {
            completions = openAiClient.getChatCompletions(request);
        } catch (FeignException e) {
            log.warn("openai api error for batch of " + chunk.size() + " items", e);
            return Collections.emptyList();
        }

        String response = completions
            .getChoices()
            .get(0)
            .getMessage()
            .getContent();

        try {
            return objectMapper.readValue(response, new TypeReference<List<LocateResponseDto>>() {});
        } catch (IOException e) {
            log.error("openai response parser error for response: " + response, e);
            return Collections.emptyList();
        }
    }

}
