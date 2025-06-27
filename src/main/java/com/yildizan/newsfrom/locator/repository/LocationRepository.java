package com.yildizan.newsfrom.locator.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.yildizan.newsfrom.locator.entity.Location;

public interface LocationRepository extends CrudRepository<Location, Integer> {

    Optional<Location> findByName(String name);

}