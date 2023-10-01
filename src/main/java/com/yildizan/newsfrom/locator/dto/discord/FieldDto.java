package com.yildizan.newsfrom.locator.dto.discord;

public record FieldDto(String name, String value, boolean inline) {

    public FieldDto(String name, String value) {
        this(name, value, true);
    }

}
