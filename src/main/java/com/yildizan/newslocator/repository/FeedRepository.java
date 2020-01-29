package com.yildizan.newslocator.repository;

import com.yildizan.newslocator.entity.Feed;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Integer> {

	@Override
	List<Feed> findAll();
	
	@Query("select f from Feed f where is_active = 1")
	List<Feed> findActive();
	
}
