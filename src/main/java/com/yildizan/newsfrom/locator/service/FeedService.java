package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    public List<Feed> findActiveFeeds() {
        return feedRepository.findActiveFeeds();
    }

}
