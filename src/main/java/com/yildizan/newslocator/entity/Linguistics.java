package com.yildizan.newslocator.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.yildizan.newslocator.utility.TimeConverter;

@Entity
public class Linguistics {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private Integer typeId;
	private Integer languageId;
	private String word;

	@Column(name = "last_dml_time", insertable = false)
	@Convert(converter=TimeConverter.class)
	private Long lastDmlTime;
	
	public Linguistics() {}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Long getLastDmlTime() {
		return lastDmlTime;
	}

	public void setLastDmlTime(Long lastDmlTime) {
		this.lastDmlTime = lastDmlTime;
	}

	@Override
	public int hashCode() {
		return id ^ typeId ^ languageId;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		else if(!(o instanceof Linguistics)) {
			return false;
		}
		else {
			return word.equals(((Linguistics) o).getWord());
		}
	}
}
