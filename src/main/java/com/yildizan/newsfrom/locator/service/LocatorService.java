package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.dto.discord.SummaryDto;
import com.yildizan.newsfrom.locator.dto.openai.LocateRequestDto;
import com.yildizan.newsfrom.locator.dto.openai.LocateResponseDto;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.utility.rss.RssReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocatorService {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final FeedService feedService;
    private final NewsService newsService;
    private final OpenAiService openAiService;

    public List<SummaryDto> bulkProcess() {
        List<SummaryDto> summaries = new ArrayList<>();
        List<BufferNews> allNews = new ArrayList<>();

        // fetch and save all news from all feeds
        for (Feed feed : feedService.findActiveFeeds()) {
            SummaryDto summary = new SummaryDto(feed, System.currentTimeMillis());
            try {
                log.info("processing feed " + feed.getUrl());
                List<BufferNews> newsList = RssReader.read(feed);
                log.info(newsList.size() + " news read");
                allNews.addAll(newsService.saveAll(newsList));
                log.info(newsList.size() + " news saved");
                summary.setCount(newsList.size());
            } catch (Exception e) {
                summary.setException(e);
            } finally {
                summary.setFinish(System.currentTimeMillis());
            }
            summaries.add(summary);
        }

        // batch locate via OpenAI
        log.info(String.format("locating %d news...", allNews.size()));
        locate(allNews);
        log.info(String.format("located %d news", allNews.size()));

        // save located news
        newsService.saveAll(allNews);

        return summaries;
    }

    private void locate(List<BufferNews> newsList) {
        List<LocateRequestDto> requestItems = newsList.stream()
            .map(news -> new LocateRequestDto(
                news.getId(),
                truncate(news.getTitle(), MAX_TITLE_LENGTH),
                truncate(news.getDescription(), MAX_DESCRIPTION_LENGTH)
            ))
            .toList();

        List<LocateResponseDto> responseItems = openAiService.askBatch(requestItems);

        Map<Long, LocateResponseDto> responseById = responseItems.stream()
            .collect(Collectors.toMap(LocateResponseDto::getId, Function.identity()));

        for (BufferNews news : newsList) {
            LocateResponseDto location = responseById.get((long) news.getId());
            if (location != null) {
                news.setPlace(location.getPlace());
                news.setLatitude(location.getLat());
                news.setLongitude(location.getLon());
            }
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

}
