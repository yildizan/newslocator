package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.Language;
import com.yildizan.newsfrom.locator.entity.Linguistics;
import com.yildizan.newsfrom.locator.entity.LinguisticsType;
import com.yildizan.newsfrom.locator.repository.LinguisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinguisticsService {

    private final LinguisticsRepository linguisticsRepository;

    private Set<String> englishConjunctions;
    private Set<String> englishExceptions;

    public boolean isConjunction(String string, Language language) {
        if (language.isEnglish()) {
            if (Objects.isNull(englishConjunctions)) {
                englishConjunctions = linguisticsRepository.findByLanguageAndLinguisticsType(language, LinguisticsType.CONJUNCTION)
                        .stream()
                        .map(Linguistics::getWord)
                        .collect(Collectors.toSet());
            }
            return englishConjunctions.contains(string);
        } else {
            throw new IllegalArgumentException("unsupported language: " + language.getCode());
        }
    }

    public boolean isException(String string, Language language) {
        if (language.isEnglish()) {
            if (Objects.isNull(englishExceptions)) {
                englishExceptions = linguisticsRepository.findByLanguageAndLinguisticsType(language, LinguisticsType.EXCEPTION)
                        .stream()
                        .map(Linguistics::getWord)
                        .collect(Collectors.toSet());
            }
            return englishExceptions.contains(string);
        } else {
            throw new IllegalArgumentException("unsupported language: " + language.getCode());
        }
    }

    public boolean notException(String string, Language language) {
        return !isException(string, language);
    }

}
