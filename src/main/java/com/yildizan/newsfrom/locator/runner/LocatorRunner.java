package com.yildizan.newsfrom.locator.runner;

import com.yildizan.newsfrom.locator.service.FeedService;
import com.yildizan.newsfrom.locator.service.LocatorService;
import com.yildizan.newsfrom.locator.utility.Discord;
import com.yildizan.newsfrom.locator.utility.Summary;
import com.yildizan.newsfrom.locator.entity.Feed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class LocatorRunner implements CommandLineRunner {

    private final Logger infoLogger = LoggerFactory.getLogger("info");
    private final Logger errorLogger = LoggerFactory.getLogger("error");

    private final Discord discord;
    private final FeedService feedService;
    private final LocatorService locatorService;

    @Override
    public void run(String... args) {
        long start = System.currentTimeMillis();

        locatorService.clear();
        infoLogger.info("buffer clear");

        List<Summary> summaries = new ArrayList<>();
        List<Feed> feeds = feedService.listActiveFeeds();
        CompletableFuture<Summary>[] futures = new CompletableFuture[feeds.size()];
        for(int i = 0; i < feeds.size(); i++) {
            futures[i] = locatorService.process(feeds.get(i));
        }
        CompletableFuture.allOf(futures).join();
        for(int i = 0; i < feeds.size(); i++) {
            Exception exception = null;
            try {
                Summary summary = futures[i].get();
                exception = summary.isSuccessful() ? null : summary.getException();
                summaries.add(summary);
            }
            catch (InterruptedException | ExecutionException e) {
                exception = e;
            }
            finally {
                if(exception != null) {
                    discord.notify(exception);
                    errorLogger.error("feedId: " + feeds.get(i).getId() + " exception: " + exception);
                }
            }
        }

        locatorService.update();
        infoLogger.info("news updated");

        long duration = System.currentTimeMillis() - start;
        infoLogger.info("execution time: " + duration + "ms feedCount: " + feeds.size());
        discord.notify(summaries, duration);
    }

}
