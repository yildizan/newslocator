package com.yildizan.newslocator.feed;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yildizan.newslocator.entity.BufferNews;
import com.yildizan.newslocator.entity.Feed;
import com.yildizan.newslocator.entity.Phrase;

public class Newspaper {
	
	private Map<BufferNews, List<Phrase>> news;
	private Feed feed;
	
	public Newspaper() {
		news = new LinkedHashMap<>();
		feed = new Feed();
	}

	public Map<BufferNews, List<Phrase>> getNews() {
		return news;
	}

	public void setNews(Map<BufferNews, List<Phrase>> news) {
		this.news = news;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}
}
