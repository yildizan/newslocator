package com.yildizan.newslocator.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.yildizan.newslocator.utility.TimeConverter;

@Entity
public class BufferNewsPhrase {

	@EmbeddedId
	private	BufferNewsPhraseId id;
	
	private Integer totalCount;

	@Column(name = "process_time", insertable = false)
	@Convert(converter=TimeConverter.class)
	private Long processTime;
	
	public BufferNewsPhrase() {}
	
	public BufferNewsPhrase(BufferNewsPhraseId id, Integer totalCount) {
		this.id = id;
		this.totalCount = totalCount;
		this.processTime = new Date().getTime();
	}

	public BufferNewsPhraseId getId() {
		return id;
	}

	public void setId(BufferNewsPhraseId id) {
		this.id = id;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Long processTime) {
		this.processTime = processTime;
	}

}
