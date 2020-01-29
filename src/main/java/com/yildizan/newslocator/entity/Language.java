package com.yildizan.newslocator.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.yildizan.newslocator.utility.TimeConverter;

@Entity
public class Language {

	// ids of languages
	public static final int TURKISH = 168;
	public static final int ENGLISH = 40;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	private String code;

	@Column(name = "last_dml_time", insertable = false)
	@Convert(converter=TimeConverter.class)
	private Long lastDmlTime;
	
	public Language() {}
	
	public Language(int id) {
		if(id == TURKISH) {
			id = 168;
			name = "Turkish";
			code = "tr";
		}
		else if(id == ENGLISH) {
			id = 40;
			name = "English";
			code = "en";
		}
		else {
			throw new java.security.InvalidParameterException();
		}
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public Long getLastDmlTime() {
		return lastDmlTime;
	}

	public void setLastDmlTime(Long lastDmlTime) {
		this.lastDmlTime = lastDmlTime;
	}
}
