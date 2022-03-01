package com.yildizan.newsfrom.locator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "wikipediaClient", url = "${wikipedia.url}")
public interface WikipediaClient {

    @GetMapping(value = "?action=query&prop=description&format=json&titles={titles}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody String research(@PathVariable("titles") String titles);

}
