package com.yildizan.newsfrom.locator.client;

import com.yildizan.newsfrom.locator.configuration.ApiClientConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "apiClient", url = "${api.url}", configuration = ApiClientConfiguration.class)
public interface ApiClient {

    @PostMapping("/feed/evict")
    void evictCache();

}
