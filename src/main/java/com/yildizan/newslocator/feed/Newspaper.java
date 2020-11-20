package com.yildizan.newslocator.feed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yildizan.newslocator.entity.BufferNews;
import com.yildizan.newslocator.entity.Feed;
import com.yildizan.newslocator.entity.Phrase;
import lombok.Data;

@Data
public class Newspaper {
	
	private Map<BufferNews, List<Phrase>> news;
	private Feed feed;
	
	public Newspaper() {
		news = new HashMap<>();
		feed = new Feed();
	}

}
