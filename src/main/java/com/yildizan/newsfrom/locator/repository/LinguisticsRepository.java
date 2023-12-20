package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.Linguistics;
import com.yildizan.newsfrom.locator.entity.LinguisticsType;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LinguisticsRepository extends CrudRepository<Linguistics, Integer> {

	List<Linguistics> findByLinguisticsType(LinguisticsType linguisticsType);

}
