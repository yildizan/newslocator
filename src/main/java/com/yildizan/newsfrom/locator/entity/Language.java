package com.yildizan.newsfrom.locator.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Language {

	@Id
	private Integer id;
	
	private String name;
	private String code;

	public boolean isTurkish() {
		return code.equals("tr");
	}

	public boolean isEnglish() {
		return code.equals("en");
	}

}
