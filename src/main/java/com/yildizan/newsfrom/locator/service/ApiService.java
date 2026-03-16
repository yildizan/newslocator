package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.client.ApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final ApiClient apiClient;

    public void evictCache() {
        try {
            apiClient.evictCache();
            log.info("cache evicted successfully");
        } catch (Exception e) {
            log.error("failed to evict cache", e);
        }
    }

}
