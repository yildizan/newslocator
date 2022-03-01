package com.yildizan.newsfrom.locator.dto.discord;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmbedDto {

    private final String title;
    private final String url;
    private FooterDto footer;
    private List<FieldDto> fields = new ArrayList<>();

}
