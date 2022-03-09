package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.dto.SummaryDto;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.entity.Language;
import com.yildizan.newsfrom.locator.entity.Phrase;
import com.yildizan.newsfrom.locator.utility.RssUtils;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocatorService {

    private final NewsService newsService;
    private final PhraseService phraseService;
    private final WikipediaService wikipediaService;

    public SummaryDto process(Feed feed)  {
        SummaryDto summary = new SummaryDto(feed, System.currentTimeMillis());
        try {
            List<BufferNews> newsList = RssUtils.read(feed);
            for (BufferNews news : newsList) {
                locate(news, feed.getLanguage());
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

    private void locate(BufferNews news, Language language) {
        List<Phrase> phrases = phraseService.extract(news, language);
        if (phrases.isEmpty()) {
            return;
        }

        log.debug("publisher: " + news.getFeed().getPublisher().getName());
        log.debug("title: " + news.getTitle());
        log.debug("description: " + news.getDescription());
        log.debug("phrases: " + phrases.stream().map(Phrase::getContent).collect(Collectors.joining(", ")));
        log.debug("\r\n");

        // descending order
        phrases.sort(Collections.reverseOrder());
        news.setTopPhrase(phrases.get(0));
        for (Phrase phrase : phrases) {
            // match original
            phraseService.match(phrase, language);
            phrase.mergeCount();
            if (!news.isLocated() && phrase.isLocated()) {
                news.setTopPhrase(phrase);
                return;
            }

            // match by dividing
            List<String> words = Arrays.stream(StringUtils.splitBySpace(phrase.getContent()))
                    .filter(StringUtils::startsWithUppercase)
                    .collect(Collectors.toList());
            for (int j = 0; j < words.size() && words.size() > 1; j++) {
                Phrase child = new Phrase(words.get(j));
                phraseService.match(child, language);
                if (child.isLocated()) {
                    news.setTopPhrase(phrase);
                    phrase.setLocation(child.getLocation());
                    return;
                }

                // match by appending
                for (int k = j + 1; k < words.size(); k++) {
                    child = new Phrase(child.getContent() + ' ' + words.get(k));
                    phraseService.match(child, language);
                    if (child.isLocated()) {
                        news.setTopPhrase(child);
                        phrase.setLocation(child.getLocation());
                        return;
                    }
                }
            }

            // match by research
            String description = wikipediaService.research(phrase.getContent());
            if (StringUtils.isNotEmpty(description)) {
                List<Phrase> researches = phraseService.extract(description, language);
                for (Phrase research : researches) {
                    phraseService.match(research, language);
                    if (research.isLocated()) {
                        news.setTopPhrase(phrase);
                        phrase.setLocation(research.getLocation());
                        return;
                    }
                }
            }
        }
    }

    private void save(BufferNews news, SummaryDto summary) {
        if (news.isLocated()) {
            phraseService.save(news.getTopPhrase());
            summary.incrementLocated();
        } else if (news.isMatched()) {
            phraseService.save(news.getTopPhrase());
            summary.incrementMatched();
        } else {
            summary.incrementNotMatched();
        }
        newsService.save(news);
    }

}
