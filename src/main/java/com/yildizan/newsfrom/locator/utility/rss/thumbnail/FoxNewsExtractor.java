package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import org.jdom2.Element;

import com.rometools.rome.feed.synd.SyndEntry;
import com.yildizan.newsfrom.locator.utility.StringUtils;

public class FoxNewsExtractor implements ThumbnailExtractor {

    @Override
    public String extract(SyndEntry entry) {
        var group = entry.getForeignMarkup()
                .stream()
                .filter(e -> e.getName().equals("group"))
                .findFirst();
        if (group.isPresent()) {
            var element = group.get().getContent()
                    .stream()
                    .filter(e -> e instanceof Element && ((Element) e).getAttributeValue("isDefault").equals("true"))
                    .findFirst();
            return element.isPresent() ? ((Element) element.get()).getAttributeValue("url") : StringUtils.emptyString();
        }
        return StringUtils.emptyString();
    }

    @Override
    public String getPublisherName() {
        return "fox news";
    }
    
}
