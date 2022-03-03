package com.yildizan.newsfrom.locator.runner;

import com.yildizan.newsfrom.locator.dto.SummaryDto;
import com.yildizan.newsfrom.locator.service.BufferService;
import com.yildizan.newsfrom.locator.service.DiscordService;
import com.yildizan.newsfrom.locator.service.FeedService;
import com.yildizan.newsfrom.locator.service.LocatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocatorRunner implements CommandLineRunner {

    private final DiscordService discordService;
    private final FeedService feedService;
    private final LocatorService locatorService;
    private final BufferService bufferService;

    @Override
    public void run(String... args) {
        bufferService.clearBuffer();
        log.info("buffer cleared");

        List<SummaryDto> summaries = new ArrayList<>();
        feedService.findActiveFeeds()
                .parallelStream()
                .forEach(feed -> summaries.add(locatorService.process(feed)));
        log.info("process completed");

        bufferService.updateNews();
        log.info("news updated");

        discordService.notify(summaries);
        log.info("discord notified");
    }

}
