package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.dto.SummaryDto;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.entity.Phrase;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import com.yildizan.newsfrom.locator.utility.rss.RssReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocatorService {

    private final FeedService feedService;
    private final NewsService newsService;
    private final PhraseService phraseService;
    private final WikipediaService wikipediaService;

    public List<SummaryDto> bulkProcess() {
        return feedService.findActiveFeeds()
            .parallelStream()
            .map(this::process)
            .toList();
    }

    public SummaryDto process(Feed feed)  {
        SummaryDto summary = new SummaryDto(feed, System.currentTimeMillis());
        try {
            List<BufferNews> newsList = RssReader.read(feed);
            for (BufferNews news : newsList) {
                locate(news);
                save(news, summary);
            }
        } catch (Exception e) {
            summary.setException(e);
        }
        finally {
            summary.setFinish(System.currentTimeMillis());
        }
        return summary;
    }

    private void locate(BufferNews news) {
        List<Phrase> phrases = phraseService.extract(news);
        if (phrases.isEmpty()) {
            return;
        }

        log.debug("publisher: " + news.getFeed().getPublisher().getName());
        log.debug("title: " + news.getTitle());
        log.debug("description: " + news.getDescription());
        log.debug("phrases: " + phrases.stream().map(Phrase::getContent).collect(Collectors.joining(", ")) + "\r\n");

        // descending order
        phrases.sort(Collections.reverseOrder());
        news.setPhrase(phrases.get(0));
        for (Phrase phrase : phrases) {
            // match original
            phraseService.match(phrase);
            phrase.mergeCount();
            if (!news.isLocated() && phrase.isLocated()) {
                news.setPhrase(phrase);
                return;
            }

            // match by dividing
            List<String> words = Arrays.stream(StringUtils.splitBySpace(phrase.getContent()))
                    .filter(StringUtils::startsWithUppercase)
                    .collect(Collectors.toList());
            for (int j = 0; j < words.size() && words.size() > 1; j++) {
                Phrase child = new Phrase(words.get(j));
                phraseService.match(child);
                if (child.isLocated()) {
                    news.setPhrase(phrase);
                    phrase.setLocation(child.getLocation());
                    return;
                }

                // match by appending
                for (int k = j + 1; k < words.size(); k++) {
                    child = new Phrase(child.getContent() + ' ' + words.get(k));
                    phraseService.match(child);
                    if (child.isLocated()) {
                        news.setPhrase(child);
                        phrase.setLocation(child.getLocation());
                        return;
                    }
                }
            }

            // match by research
            String description = wikipediaService.research(phrase.getContent());
            if (StringUtils.isNotEmpty(description)) {
                List<Phrase> researches = phraseService.extract(description);
                for (Phrase research : researches) {
                    phraseService.match(research);
                    if (research.isLocated()) {
                        news.setPhrase(phrase);
                        phrase.setLocation(research.getLocation());
                        return;
                    }
                }
            }
        }
    }

    private void save(BufferNews news, SummaryDto summary) {
        if (news.isLocated()) {
            phraseService.save(news.getPhrase());
            summary.incrementLocated();
        } else if (news.isMatched()) {
            phraseService.save(news.getPhrase());
            summary.incrementMatched();
        } else {
            summary.incrementNone();
        }
        newsService.save(news);
    }

}
