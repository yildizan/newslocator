package com.yildizan.newsfrom.locator.utility;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.entity.Publisher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jdom.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RssUtils {

    public static List<BufferNews> read(Feed feed) throws IOException, FeedException {
        List<BufferNews> newsList = new ArrayList<>();

        URL url = new URL(feed.getUrl());
        XmlReader reader = new XmlReader(url);
        SyndFeed rss = new SyndFeedInput().build(reader);
        for (SyndEntry entry : (List<SyndEntry>) rss.getEntries()) {
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
                SyndEnclosure enclosure = ((List<SyndEnclosure>) entry.getEnclosures())
                        .stream()
                        .filter(e -> e.getType().equals("image/jpeg"))
                        .findFirst()
                        .orElseThrow();
                return enclosure.getUrl();
            } else if (publisher.isBbc()) {
                Element element = ((List<Element>) entry.getForeignMarkup())
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
                Element element = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("thumbnail"))
                        .findFirst()
                        .orElseThrow();
                return element.getAttributeValue("url");
            } else if (publisher.isNewYorkTimes()) {
                Element element = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("content"))
                        .findFirst()
                        .orElseThrow();
                return element.getAttributeValue("url");
            } else if (publisher.isFoxNews()) {
                Element group = ((List<Element>) entry.getForeignMarkup())
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
