package com.yildizan.newsfrom.locator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Feed {

	@Id
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	private String url;
	private boolean isActive;

}
