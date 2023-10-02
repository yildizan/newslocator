package com.yildizan.newsfrom.locator.utility;

import java.sql.Timestamp;

import jakarta.persistence.AttributeConverter;

public class TimeConverter implements AttributeConverter<Long, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(Long time) {
		return new Timestamp(time);
	}

	@Override
	public Long convertToEntityAttribute(Timestamp time) {
		return time.getTime();
	}

}
