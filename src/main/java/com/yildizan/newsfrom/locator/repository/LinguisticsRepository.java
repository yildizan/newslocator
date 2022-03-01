package com.yildizan.newsfrom.locator.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.yildizan.newsfrom.locator.entity.Linguistics;

public interface LinguisticsRepository extends CrudRepository<Linguistics, Integer> {

	@Override
	List<Linguistics> findAll();
	
	@Query("select l from Linguistics l where l.typeId = 1 and l.languageId = 40")
	List<Linguistics> findEnglishConjunctions();

	@Query("select l from Linguistics l where l.typeId = 2 and l.languageId = 40")
	List<Linguistics> findEnglishExceptions();
}
