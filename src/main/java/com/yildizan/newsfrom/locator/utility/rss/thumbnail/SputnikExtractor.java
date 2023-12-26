package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import com.rometools.rome.feed.synd.SyndEntry;
import com.yildizan.newsfrom.locator.utility.StringUtils;

public class SputnikExtractor implements ThumbnailExtractor {

    @Override
    public String extract(SyndEntry entry) {
        var enclosure = entry.getEnclosures()
                .stream()
                .filter(e -> e.getType().equals("image/jpeg"))
                .findFirst();
        return enclosure.isPresent() ? enclosure.get().getUrl() : StringUtils.emptyString();
    }

    @Override
    public String getPublisherName() {
        return "sputnik";
    }
    
}
