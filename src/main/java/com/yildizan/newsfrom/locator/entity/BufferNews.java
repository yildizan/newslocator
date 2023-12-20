package com.yildizan.newsfrom.locator.entity;

import com.yildizan.newsfrom.locator.utility.TimeConverter;

import java.util.Objects;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class BufferNews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@ManyToOne
	@JoinColumn(name = "phrase_id")
	private Phrase phrase;

	private String title;
	private String description;
	private String link;
	private String thumbnailUrl;

	@Convert(converter = TimeConverter.class)
	private Long publishDate;

	public boolean isMatched() {
		return Objects.nonNull(phrase);
	}
	
	public boolean isLocated() {
		return isMatched() && phrase.isLocated();
	}

}
