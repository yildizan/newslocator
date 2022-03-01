package com.yildizan.newsfrom.locator.dto.discord;

import lombok.Data;

@Data
public class FieldDto {

    private String name;
    private String value;
    private final boolean inline = true;

}
