package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.BufferNews;

import org.springframework.data.repository.CrudRepository;

public interface NewsRepository extends CrudRepository<BufferNews, Integer> {
	
}
