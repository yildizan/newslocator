package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.Linguistics;
import com.yildizan.newsfrom.locator.entity.LinguisticsType;
import com.yildizan.newsfrom.locator.repository.LinguisticsRepository;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinguisticsService {

    private Set<String> conjunctions;
    private Set<String> exceptions;

    @Autowired
    public LinguisticsService(LinguisticsRepository linguisticsRepository) {
        this.conjunctions = linguisticsRepository.findByLinguisticsType(LinguisticsType.CONJUNCTION)
            .stream()
            .map(Linguistics::getWord)
            .collect(Collectors.toSet());
        this.exceptions = linguisticsRepository.findByLinguisticsType(LinguisticsType.EXCEPTION)
            .stream()
            .map(Linguistics::getWord)
            .collect(Collectors.toSet());
    }

    public boolean isConjunction(String string) {
        return conjunctions.contains(string);
    }

    public boolean isException(String string) {
        return exceptions.contains(string);
    }

    public boolean notException(String string) {
        return !isException(string);
    }

}
