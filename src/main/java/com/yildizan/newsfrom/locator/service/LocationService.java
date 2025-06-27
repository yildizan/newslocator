package com.yildizan.newsfrom.locator.service;

import org.springframework.stereotype.Service;

import com.yildizan.newsfrom.locator.entity.Location;
import com.yildizan.newsfrom.locator.repository.LocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location findByName(String name) {
        return locationRepository.findByName(name).orElse(null);
    }

    public void save(Location location) {
        locationRepository.save(location);
    }
    
}
