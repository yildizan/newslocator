package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import com.rometools.rome.feed.synd.SyndEntry;
import com.yildizan.newsfrom.locator.utility.StringUtils;

public class NewYorkTimesExtractor implements ThumbnailExtractor {

    @Override
    public String extract(SyndEntry entry) {
        var element = entry.getForeignMarkup()
                .stream()
                .filter(e -> e.getName().equals("content"))
                .findFirst();
        return element.isPresent() ? element.get().getAttributeValue("url") : StringUtils.emptyString();
    }

    @Override
    public String getPublisherName() {
        return "new york times";
    }
    
}
