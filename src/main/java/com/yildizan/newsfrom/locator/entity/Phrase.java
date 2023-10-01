package com.yildizan.newsfrom.locator.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Phrase implements Comparable<Phrase> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location location;
	
	@Column(name = "content", updatable = false)
	private String content;
	
	private int count;

	@Transient
	private int currentCount;
	
	public Phrase(String content) {
		this.currentCount = 1;
		this.count = 0;
		this.content = content;
	}
	
	public void incrementCount() {
		currentCount++;
	}
	
	public boolean isLocated() {
		return Objects.nonNull(location);
	}

	public void merge(Phrase phrase) {
		id = phrase.getId();
		location = phrase.getLocation();
		count = phrase.getCount();
	}

	public void mergeCount() {
		count += currentCount;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof String) {
			return content.equals(o);
		} else if (o instanceof Phrase) {
			return content.equals(((Phrase) o).getContent());
		} else {
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
