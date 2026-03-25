package com.yildizan.newsfrom.locator.runner;

import com.yildizan.newsfrom.locator.dto.discord.SummaryDto;
import com.yildizan.newsfrom.locator.service.ApiService;
import com.yildizan.newsfrom.locator.service.BufferService;
import com.yildizan.newsfrom.locator.service.DiscordService;
import com.yildizan.newsfrom.locator.service.LocatorService;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocatorRunner implements CommandLineRunner {

    private final ApiService apiService;
    private final DiscordService discordService;
    private final LocatorService locatorService;
    private final BufferService bufferService;

    @Override
    public void run(String... args) {
        boolean dryRun = Arrays.stream(args).anyMatch("--dry-run"::equals);

        long tick = System.currentTimeMillis();
        log.info("clearing buffer...");
        if (!dryRun) bufferService.clearBuffer();
        long tock = System.currentTimeMillis();
        log.info(String.format("cleared buffer in %d ms", tock - tick));

        tick = System.currentTimeMillis();
        log.info("processing...");
        SummaryDto summary = locatorService.bulkProcess();
        tock = System.currentTimeMillis();
        log.info(String.format("processed in %d ms", tock - tick));

        if (summary.isSuccessful()) {
            tick = System.currentTimeMillis();
            log.info("flushing buffer...");
            if (!dryRun) bufferService.flushBuffer();
            tock = System.currentTimeMillis();
            log.info(String.format("flushed buffer in %d ms", tock - tick));

            tick = System.currentTimeMillis();
            log.info("evicting cache...");
            if (!dryRun) apiService.evictCache();
            tock = System.currentTimeMillis();
            log.info(String.format("evicted cache in %d ms", tock - tick));
        } else {
            log.error("error detected...");
        }

        tick = System.currentTimeMillis();
        log.info("notifying...");
        discordService.notify(summary);
        tock = System.currentTimeMillis();
        log.info(String.format("notified in %d ms", tock - tick));
    }

}
