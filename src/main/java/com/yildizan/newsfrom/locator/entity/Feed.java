package com.yildizan.newsfrom.locator.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Feed {

	@Id
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@ManyToOne
	@JoinColumn(name = "language_id")
	private Language language;

	private String url;
	private boolean isActive;

}
