package com.yildizan.newslocator.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.yildizan.newslocator.entity.Linguistics;

public interface LinguisticsRepository extends CrudRepository<Linguistics, Integer> {

	@Override
	List<Linguistics> findAll();
	
	@Query("select l from Linguistics l where type_id = 1 and language_id = 40")
	List<Linguistics> findEnglishConjunctions();

	@Query("select l from Linguistics l where type_id = 2 and language_id = 40")
	List<Linguistics> findEnglishExceptions();
}
