package com.yildizan.newsfrom.locator.entity;

import com.yildizan.newsfrom.locator.utility.TimeConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
public class BufferNews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String title;
	private String description;
	private String link;
	private String thumbnailUrl;

	@Convert(converter = TimeConverter.class)
	private Long publishDate;

	@ManyToOne
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@ManyToOne
	@JoinColumn(name = "top_phrase_id")
	private Phrase topPhrase;

	public boolean isMatched() {
		return Objects.nonNull(topPhrase);
	}
	
	public boolean isLocated() {
		return isMatched() && topPhrase.isLocated();
	}

}
