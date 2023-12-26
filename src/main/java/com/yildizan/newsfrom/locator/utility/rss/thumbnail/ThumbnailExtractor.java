package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import com.rometools.rome.feed.synd.SyndEntry;

public interface ThumbnailExtractor {
    
    String extract(SyndEntry entry);
    String getPublisherName();

}
