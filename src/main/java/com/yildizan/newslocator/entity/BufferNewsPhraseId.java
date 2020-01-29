package com.yildizan.newslocator.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class BufferNewsPhraseId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer newsId;
	private Integer phraseId;
	
	public BufferNewsPhraseId() {}
	
	public BufferNewsPhraseId(Integer newsId, Integer phraseId) {
		this.setNewsId(newsId);
		this.setPhraseId(phraseId);
	}

	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}

	public Integer getPhraseId() {
		return phraseId;
	}

	public void setPhraseId(Integer phraseId) {
		this.phraseId = phraseId;
	}

	@Override
	public int hashCode() {
		return newsId ^ phraseId;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		else if(o instanceof BufferNewsPhraseId) {
			return ((BufferNewsPhraseId) o).getNewsId() == this.newsId &&
					((BufferNewsPhraseId) o).getPhraseId() == this.phraseId;
		}
		else {
			return false;
		}
	}
}
