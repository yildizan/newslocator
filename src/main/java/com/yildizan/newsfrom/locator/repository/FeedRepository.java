package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.Feed;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Integer> {
	
	@Query("select f from Feed f where f.isActive = 1")
	List<Feed> findActive();
	
}
