package com.yildizan.newsfrom.locator.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocateResponseDto {

    private long id;
    private double lat;
    private double lon;
    private String place;

}
