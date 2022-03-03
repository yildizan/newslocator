package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.Phrase;
import com.yildizan.newsfrom.locator.repository.PhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheablePhraseService {

    private final PhraseRepository phraseRepository;

    @Cacheable(value = "phrases", key = "#content", unless = "#result == null")
    public Phrase find(String content) {
        return phraseRepository.findByContent(content).orElse(null);
    }

}
