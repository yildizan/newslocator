package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public void save(BufferNews news) {
        newsRepository.save(news);
    }

}
