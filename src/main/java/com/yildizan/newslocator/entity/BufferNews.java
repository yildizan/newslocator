package com.yildizan.newslocator.entity;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.yildizan.newslocator.utility.TimeConverter;
import lombok.Data;

@Data
@Entity
public class BufferNews {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private int feedId;
	private Integer topPhraseId;
	private String title;
	private String description;
	private String link;
	private String thumbnailUrl;
	
	@Transient
	private Integer index;

	@Convert(converter = TimeConverter.class)
	private Long publishDate;
	
	public boolean isMatched() {
		return topPhraseId != null && topPhraseId > 0;
	}
}
