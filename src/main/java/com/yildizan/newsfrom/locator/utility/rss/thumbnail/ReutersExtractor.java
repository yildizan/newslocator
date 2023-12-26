package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import com.rometools.rome.feed.synd.SyndEntry;
import com.yildizan.newsfrom.locator.utility.StringUtils;

public class ReutersExtractor implements ThumbnailExtractor {

    @Override
    public String extract(SyndEntry entry) {
        return StringUtils.emptyString();
    }

    @Override
    public String getPublisherName() {
        return "reuters";
    }
    
}
