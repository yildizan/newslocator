package com.yildizan.newslocator.repository;

import com.yildizan.newslocator.entity.Phrase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PhraseRepository extends CrudRepository<Phrase, Integer> {
	
	Optional<Phrase> findByContent(String content);
	
}
