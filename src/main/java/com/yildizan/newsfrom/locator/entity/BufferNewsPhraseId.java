package com.yildizan.newsfrom.locator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class BufferNewsPhraseId implements Serializable {

	private static final long serialVersionUID = 1L;

	private int newsId;
	private int phraseId;

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
			return ((BufferNewsPhraseId) o).getNewsId() == this.newsId && ((BufferNewsPhraseId) o).getPhraseId() == this.phraseId;
		}
		else {
			return false;
		}
	}
}
