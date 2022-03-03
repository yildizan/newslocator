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
        long tick = System.currentTimeMillis();
        log.info("clearing buffer...");

        bufferService.clearBuffer();

        long tock = System.currentTimeMillis();
        log.info(String.format("cleared buffer in %d ms", tock - tick));

        tick = System.currentTimeMillis();
        log.info("processing...");

        List<SummaryDto> summaries = new ArrayList<>();
        feedService.findActiveFeeds()
                .parallelStream()
                .forEach(feed -> summaries.add(locatorService.process(feed)));

        tock = System.currentTimeMillis();
        log.info(String.format("processed in %d ms", tock - tick));

        tick = System.currentTimeMillis();
        log.info("updating news...");

        bufferService.updateNews();

        tock = System.currentTimeMillis();
        log.info(String.format("updated news in %d ms", tock - tick));

        tick = System.currentTimeMillis();
        log.info("notifying...");

        discordService.notify(summaries);

        tock = System.currentTimeMillis();
        log.info(String.format("notified in %d ms", tock - tick));
    }

}
