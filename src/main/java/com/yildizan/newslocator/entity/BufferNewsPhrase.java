package com.yildizan.newslocator.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BufferNewsPhrase {

	@EmbeddedId
	private	BufferNewsPhraseId id;
	
	private int totalCount;

}
