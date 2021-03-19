package com.yildizan.newslocator.service;

import com.yildizan.newslocator.entity.Feed;
import com.yildizan.newslocator.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    public List<Feed> listActiveFeeds() {
        return feedRepository.findActive();
    }

}
