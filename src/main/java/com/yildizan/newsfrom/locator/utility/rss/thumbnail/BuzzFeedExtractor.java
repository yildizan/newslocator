package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import com.rometools.rome.feed.synd.SyndEntry;

public class BuzzFeedExtractor implements ThumbnailExtractor {

    @Override
    public String extract(SyndEntry entry) {
        String description = entry.getDescription().getValue();
        int index = description.indexOf("src=\"", description.indexOf("<img ")) + "src=\"".length();
        return description.substring(index, description.indexOf("\"", index + 1));
    }

    @Override
    public String getPublisherName() {
        return "buzzfeed";
    }
    
}
