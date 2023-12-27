package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Phrase;
import com.yildizan.newsfrom.locator.repository.PhraseRepository;
import com.yildizan.newsfrom.locator.utility.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhraseService {

    private final PhraseRepository phraseRepository;
    private final LinguisticsService linguisticsService;
    private final CacheablePhraseService cacheablePhraseService;

    public void save(Phrase phrase) {
        phraseRepository.save(phrase);
        cacheablePhraseService.evict(phrase.getContent());
    }

    public void match(Phrase phrase) {
        if (linguisticsService.isException(phrase.getContent())) {
            return;
        }

        Phrase result = cacheablePhraseService.find(phrase.getContent());
        if (Objects.nonNull(result)) {
            phrase.merge(result);
        }
    }

    public List<Phrase> extract(BufferNews news) {
        return extract(Objects.requireNonNullElse(news.getDescription(), news.getTitle()));
    }

    public List<Phrase> extract(String text) {
        List<Phrase> phrases = new ArrayList<>();
        String[] words = StringUtils.splitBySpace(text.trim());
        String content = StringUtils.emptyString();
        boolean endPhrase;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            endPhrase = i == words.length - 1 || StringUtils.endsWithPunctuation(word) || StringUtils.endsWithPossessive(word);

            if (StringUtils.startsWithUppercase(word)) {
                word = StringUtils.cleanSuffix(word);
                content = content.isEmpty() ? word : content + ' ' + word;
            } else {
                // check conjunction
                if (StringUtils.isNotEmpty(content) && linguisticsService.isConjunction(word)) {
                    boolean allConjunction = true;
                    for (int j = 0; j < words.length - i - 1; j++) {
                        allConjunction = linguisticsService.isConjunction(words[i + j]);
                        if (!allConjunction || StringUtils.startsWithUppercase(words[i + j + 1])) {
                            break;
                        }
                    }
                    if (allConjunction) {
                        content = content + ' ' + word;
                    } else {
                        endPhrase = true;
                    }
                } else {
                    endPhrase = true;
                }
            }

            if (endPhrase && StringUtils.isNotEmpty(content)) {
                if (linguisticsService.notException(content)) {
                    final String finalContent = content;
                    phrases.stream()
                            .filter(p -> p.getContent().equals(finalContent))
                            .findAny()
                            .ifPresentOrElse(Phrase::incrementCount, () -> phrases.add(new Phrase(finalContent)));
                }
                content = StringUtils.emptyString();
            }
        }
        return phrases;
    }

}
