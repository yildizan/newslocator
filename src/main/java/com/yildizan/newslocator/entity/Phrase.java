package com.yildizan.newslocator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
public class Phrase implements Comparable<Phrase> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private Integer locationId;
	
	@Column(name = "content", updatable = false)
	private String content;
	
	@Transient
	private int currentCount;
	
	private int totalCount;
	
	public Phrase() {
		currentCount = 1;
		totalCount = 1;
	}
	
	public Phrase(String content) {
		this();
		this.content = content;
	}
	
	public void addCount(int count) {
		this.currentCount += count;
		this.totalCount += count;
	}
	
	public boolean hasLocation() {
		return this.locationId != null && this.locationId > 0;
	}

	@Override
	public int hashCode() {
		return id ^ locationId;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		else if(o instanceof String) {
			return content.equals(o);
		}
		else if(o instanceof Phrase) {
			return content.equals(((Phrase) o).getContent());
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return content;
	}

	@Override
	public int compareTo(Phrase o) {
		return this.currentCount - o.currentCount;
	}
}
