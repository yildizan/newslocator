package com.yildizan.newsfrom.locator.utility.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.yildizan.newsfrom.locator.entity.BufferNews;
import com.yildizan.newsfrom.locator.entity.Feed;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import com.yildizan.newsfrom.locator.utility.rss.thumbnail.ThumbnailExtractor;
import com.yildizan.newsfrom.locator.utility.rss.thumbnail.ThumbnailExtractorFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RssReader {

    public static List<BufferNews> read(Feed feed) throws IOException, FeedException {
        List<BufferNews> newsList = new ArrayList<>();
        ThumbnailExtractor thumbnailExtractor = ThumbnailExtractorFactory.provide(feed.getPublisher().getName());

        SyndFeed rss = loadFeed(feed.getUrl());
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
            news.setThumbnailUrl(thumbnailExtractor.extract(entry));
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

    private static SyndFeed loadFeed(String url) throws IOException, FeedException {
        SyndFeed rss = null;

        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            HttpUriRequest request = new HttpGet(url);
            try (
                CloseableHttpResponse response = client.execute(request);
                InputStream stream = response.getEntity().getContent()
            ) {
              SyndFeedInput input = new SyndFeedInput();
              rss = input.build(new XmlReader(stream));
            }
        }
        
        if (rss == null) {
            throw new FeedException(String.format("unable to fetch: %s", url));
        }
        
        return rss;
    }

}
