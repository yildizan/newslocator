package com.yildizan.newsfrom.locator.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Feed {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int publisherId;
	private int categoryId;
	private int languageId;
	private String url;
	private int isActive;

}
