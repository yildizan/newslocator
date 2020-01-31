package com.yildizan.newslocator.repository;

import com.yildizan.newslocator.entity.Feed;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Integer> {
	
	@Query("select f from Feed f where f.isActive = 1")
	List<Feed> findActive();
	
}
