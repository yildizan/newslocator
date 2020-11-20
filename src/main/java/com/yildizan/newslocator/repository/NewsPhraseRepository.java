package com.yildizan.newslocator.repository;

import com.yildizan.newslocator.entity.BufferNewsPhrase;

import org.springframework.data.repository.CrudRepository;

public interface NewsPhraseRepository extends CrudRepository<BufferNewsPhrase, Integer> {

}
