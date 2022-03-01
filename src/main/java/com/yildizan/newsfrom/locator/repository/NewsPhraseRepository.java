package com.yildizan.newsfrom.locator.repository;

import com.yildizan.newsfrom.locator.entity.BufferNewsPhrase;

import org.springframework.data.repository.CrudRepository;

public interface NewsPhraseRepository extends CrudRepository<BufferNewsPhrase, Integer> {

}
