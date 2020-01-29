package com.yildizan.newslocator.repository;

import com.yildizan.newslocator.entity.BufferNews;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface NewsRepository extends CrudRepository<BufferNews, Integer> {

	@Override
	List<BufferNews> findAll();
	
}
