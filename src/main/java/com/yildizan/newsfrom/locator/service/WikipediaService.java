package com.yildizan.newsfrom.locator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yildizan.newsfrom.locator.client.WikipediaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WikipediaService {

    private final WikipediaClient wikipediaClient;

    public String research(String text) {
        String response = wikipediaClient.research(UriUtils.encode(text, StandardCharsets.UTF_8));

        try {
            return new ObjectMapper().readTree(response)
                    .findValue("description")
                    .textValue();
        } catch (IOException | NullPointerException e) {
            return "";
        }
    }

}
