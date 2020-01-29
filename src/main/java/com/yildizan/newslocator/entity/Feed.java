package com.yildizan.newslocator.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.yildizan.newslocator.utility.TimeConverter;

@Entity
public class Feed {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private Integer publisherId;
	private Integer categoryId;
	private Integer languageId;
	private String url;
	private Integer isActive;

	@Column(name = "last_dml_time", insertable = false)
	@Convert(converter=TimeConverter.class)
	private Long lastDmlTime;
	
	public Feed() {}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getPublisherId() {
		return publisherId;
	}
	
	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}
	
	public Integer getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	public Integer getLanguageId() {
		return languageId;
	}
	
	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getIsActive() {
		return isActive;
	}
	
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	
	public Long getLastDmlTime() {
		return lastDmlTime;
	}
	
	public void setLastDmlTime(Long lastDmlTime) {
		this.lastDmlTime = lastDmlTime;
	}	
}
