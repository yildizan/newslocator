package com.yildizan.newsfrom.locator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yildizan.newsfrom.locator.client.WikipediaClient;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j(topic = "wikipedia")
@Service
@RequiredArgsConstructor
public class WikipediaService {

    private final WikipediaClient wikipediaClient;

    @Value("${wikipedia.enabled}")
    private boolean enabled;

    public String research(String text) {
        if (!enabled) {
            return StringUtils.emptyString();
        }

        try {
            String response = wikipediaClient.research(UriUtils.encode(text, StandardCharsets.UTF_8));
            JsonNode description = new ObjectMapper().readTree(response).findValue("description");
            return Objects.nonNull(description) ? description.textValue() : StringUtils.emptyString();
        } catch (IOException | NullPointerException | FeignException e) {
            log.warn("wikipedia api error for text: " + text, e);
            return StringUtils.emptyString();
        }
    }

}
