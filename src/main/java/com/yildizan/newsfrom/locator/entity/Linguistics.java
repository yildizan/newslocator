package com.yildizan.newsfrom.locator.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Linguistics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String word;

	@ManyToOne
	@JoinColumn(name = "language_id")
	private Language language;

	@Enumerated(EnumType.STRING)
	private LinguisticsType linguisticsType;

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Linguistics) {
			return word.equals(((Linguistics) o).getWord());
		} else {
			return false;
		}
	}
}
