package com.yildizan.newsfrom.locator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
