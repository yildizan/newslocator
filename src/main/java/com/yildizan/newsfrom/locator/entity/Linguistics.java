package com.yildizan.newsfrom.locator.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Linguistics {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private int typeId;
	private int languageId;
	private String word;

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
