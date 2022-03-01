package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.Phrase;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PhraseRepository extends CrudRepository<Phrase, Integer> {
	
	Optional<Phrase> findByContent(String content);
	
}
