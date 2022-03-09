package com.yildizan.newsfrom.locator.utility;

import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.entity.Publisher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jdom2.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RssUtils {

    public static List<BufferNews> read(Feed feed) throws Exception {
        List<BufferNews> newsList = new ArrayList<>();

        URL url = new URL(feed.getUrl());
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed rss = input.build(new XmlReader(url));
        for (SyndEntry entry : rss.getEntries()) {
            BufferNews news = new BufferNews();
            news.setTitle(StringUtils.cleanCode(entry.getTitle()));
            news.setLink(entry.getLink());
            if (Objects.isNull(entry.getPublishedDate())) {
                news.setPublishDate(System.currentTimeMillis());
            } else {
                news.setPublishDate(entry.getPublishedDate().getTime());
            }
            news.setFeed(feed);
            news.setThumbnailUrl(extractThumbnail(entry, feed.getPublisher()));
            if (news.getThumbnailUrl().isEmpty() && Objects.nonNull(rss.getImage())) {
                news.setThumbnailUrl(rss.getImage().getUrl());
            }
            if (Objects.nonNull(entry.getDescription()) && !StringUtils.cleanCode(entry.getDescription().getValue()).isEmpty()) {
                news.setDescription(StringUtils.cleanCode(entry.getDescription().getValue()));
            }
            newsList.add(news);
        }

        return newsList;
    }

    private static String extractThumbnail(SyndEntry entry, Publisher publisher) {
        try {
            if (publisher.isSputnik()) {
                SyndEnclosure enclosure = entry.getEnclosures()
                        .stream()
                        .filter(e -> e.getType().equals("image/jpeg"))
                        .findFirst()
                        .orElseThrow();
                return enclosure.getUrl();
            } else if (publisher.isBbc()) {
                Element element = entry.getForeignMarkup()
                        .stream()
                        .filter(e -> e.getName().equals("thumbnail"))
                        .findFirst()
                        .orElseThrow();
                return element.getAttributeValue("url");
            } else if (publisher.isBuzzFeed()) {
                String description = entry.getDescription().getValue();
                int index = description.indexOf("src=\"", description.indexOf("<img ")) + "src=\"".length();
                return description.substring(index, description.indexOf("\"", index + 1));
            } else if (publisher.isWashingtonPost()) {
                Element element = entry.getForeignMarkup()
                        .stream()
                        .filter(e -> e.getName().equals("thumbnail"))
                        .findFirst()
                        .orElseThrow();
                return element.getAttributeValue("url");
            } else if (publisher.isNewYorkTimes()) {
                Element element = entry.getForeignMarkup()
                        .stream()
                        .filter(e -> e.getName().equals("content"))
                        .findFirst()
                        .orElseThrow();
                return element.getAttributeValue("url");
            } else if (publisher.isFoxNews()) {
                Element group = entry.getForeignMarkup()
                        .stream()
                        .filter(e -> e.getName().equals("group"))
                        .findFirst()
                        .orElseThrow();
                Element element = ((Element) group.getContent()
                        .stream()
                        .filter(e -> e instanceof Element && ((Element) e).getAttributeValue("isDefault").equals("true"))
                        .findFirst()
                        .orElseThrow());
                return element.getAttributeValue("url");
            }
        }
        catch (Exception ignored) {

        }
        return StringUtils.emptyString();
    }

}
