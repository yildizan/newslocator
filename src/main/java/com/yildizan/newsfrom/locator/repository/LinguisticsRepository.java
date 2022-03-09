package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.Linguistics;
import com.yildizan.newsfrom.locator.entity.LinguisticsType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LinguisticsRepository extends CrudRepository<Linguistics, Integer> {

	List<Linguistics> findByLanguageCodeAndLinguisticsType(String languageCode, LinguisticsType linguisticsType);

}
